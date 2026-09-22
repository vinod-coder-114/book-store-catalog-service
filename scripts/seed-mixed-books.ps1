param(
    [int]$Count = 10,
    [string]$MongoContainer = "catalog-service-mongodb",
    [string]$MongoUri = "mongodb://catalog_user:catalog_password@localhost:27017/catalog_db?authSource=catalog_db",
    [string]$CatalogBaseUrl = "http://localhost:8082",
    [string]$BearerToken = "",
    [switch]$DryRun
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

if ($Count -lt 1) {
    throw "Count must be at least 1"
}

$rootDir = Split-Path -Parent $PSScriptRoot
$dataDir = Join-Path $rootDir "seed-data"
New-Item -ItemType Directory -Force -Path $dataDir | Out-Null

$booksJsonPath = Join-Path $dataDir "books-mixed-$Count.json"
$mongoUpsertPath = Join-Path $dataDir "mongo-books-upsert.generated.js"
$tempDir = Join-Path $env:TEMP "catalog-seed-images"
New-Item -ItemType Directory -Force -Path $tempDir | Out-Null

$subjects = @(
    @{ slug = "fantasy"; genre = "Fantasy" },
    @{ slug = "science_fiction"; genre = "Science Fiction" },
    @{ slug = "history"; genre = "History" },
    @{ slug = "business"; genre = "Business" },
    @{ slug = "self_help"; genre = "Self Help" },
    @{ slug = "mystery"; genre = "Mystery" },
    @{ slug = "biography"; genre = "Biography" },
    @{ slug = "programming"; genre = "Programming" },
    @{ slug = "psychology"; genre = "Psychology" },
    @{ slug = "philosophy"; genre = "Philosophy" },
    @{ slug = "romance"; genre = "Romance" },
    @{ slug = "thriller"; genre = "Thriller" },
    @{ slug = "economics"; genre = "Economics" },
    @{ slug = "art"; genre = "Art" }
)

function Get-SubjectJson {
    param([string]$SubjectSlug)

    $url = "https://openlibrary.org/subjects/$SubjectSlug.json?limit=30"
    $raw = & curl.exe -sL $url
    if (-not $raw) {
        throw "No response for subject '$SubjectSlug'"
    }

    return ($raw | ConvertFrom-Json)
}

function New-BookFromSubject {
    param(
        [pscustomobject]$Subject,
        [int]$Ordinal,
        [System.Collections.Generic.HashSet[string]]$UsedTitles
    )

    $subjectPayload = Get-SubjectJson -SubjectSlug $Subject.slug
    $candidate = $subjectPayload.works |
        Where-Object {
            $_.cover_id -and
            $_.title -and
            $_.authors -and
            $_.authors[0].name -and
            (-not $UsedTitles.Contains([string]$_.title))
        } |
        Select-Object -First 1

    if (-not $candidate) {
        throw "No suitable book candidate found for subject '$($Subject.slug)'"
    }

    $title = ([string]$candidate.title -replace "[^\x20-\x7E]", "").Trim()
    $author = ([string]$candidate.authors[0].name -replace "[^\x20-\x7E]", "").Trim()
    if ([string]::IsNullOrWhiteSpace($title)) {
        throw "Subject '$($Subject.slug)' returned an empty normalized title"
    }
    if ([string]::IsNullOrWhiteSpace($author)) {
        $author = "Unknown"
    }
    $coverId = [int]$candidate.cover_id
    $listPrice = 350 + (($coverId + ($Ordinal * 29)) % 650)
    $salePrice = [Math]::Max(199, $listPrice - [int][Math]::Floor($listPrice * 0.18))
    $averageRating = [Math]::Round((3.9 + (($Ordinal % 9) * 0.1)), 1)
    $ratingCount = 800 + ($Ordinal * 173)

    $bookId = "bk_mx_{0:D3}" -f $Ordinal
    $imageUrl = "https://covers.openlibrary.org/b/id/$coverId-L.jpg"

    return [PSCustomObject]@{
        _id = $bookId
        title = $title
        author = $author
        genre = [string]$Subject.genre
        format = "Paperback"
        pricing = [PSCustomObject]@{
            currency = "INR"
            salePrice = $salePrice
            listPrice = $listPrice
        }
        rating = [PSCustomObject]@{
            average = $averageRating
            count = $ratingCount
        }
        sourceImageUrl = $imageUrl
    }
}

$selectedBooks = New-Object System.Collections.Generic.List[object]
$usedTitles = New-Object System.Collections.Generic.HashSet[string]
$ordinal = 1

foreach ($subject in $subjects) {
    if ($selectedBooks.Count -ge $Count) {
        break
    }

    try {
        $book = New-BookFromSubject -Subject $subject -Ordinal $ordinal -UsedTitles $usedTitles
        $selectedBooks.Add($book)
        $null = $usedTitles.Add([string]$book.title)
        $ordinal++
    }
    catch {
        Write-Warning "Skipping subject '$($subject.slug)': $($_.Exception.Message)"
    }
}

if ($selectedBooks.Count -lt $Count) {
    throw "Only collected $($selectedBooks.Count) books from internet; expected $Count"
}

$books = $selectedBooks.ToArray()
$books | ConvertTo-Json -Depth 8 | Set-Content -Path $booksJsonPath -Encoding UTF8

$booksJsonEscaped = $books | ConvertTo-Json -Depth 8
$mongoScript = @"
use('catalog_db');

const books = $booksJsonEscaped;

const operations = books.map((b) => ({
  updateOne: {
    filter: { _id: b._id },
    update: {
      `$set: {
        title: b.title,
        author: b.author,
        genre: b.genre,
        format: b.format,
        pricing: b.pricing,
        rating: b.rating,
        images: {
          front: { card: b.sourceImageUrl, thumb: b.sourceImageUrl },
          back: { card: b.sourceImageUrl, thumb: b.sourceImageUrl },
          aspectRatio: 0.75,
          blurHash: ""
        }
      }
    },
    upsert: true
  }
}));

if (operations.length > 0) {
  db.books.bulkWrite(operations, { ordered: false });
}

db.books.createIndex({ title: "text", author: "text" });
printjson({ seeded: books.length, mode: "upsert" });
"@

$mongoScript | Set-Content -Path $mongoUpsertPath -Encoding UTF8
Write-Host "Generated: $booksJsonPath"
Write-Host "Generated: $mongoUpsertPath"

if ($DryRun) {
    Write-Host "DryRun enabled. Skipping Mongo upsert and image upload."
    return
}

$null = & docker info 2>$null
if ($LASTEXITCODE -ne 0) {
    throw "Docker daemon is not reachable. Start Docker Desktop and rerun."
}

$mongoScript | docker exec -i $MongoContainer mongosh $MongoUri --quiet | Out-Host
if ($LASTEXITCODE -ne 0) {
    throw "Mongo upsert failed. Verify container '$MongoContainer' is running and Mongo credentials are valid."
}

foreach ($book in $books) {
    $bookId = [string]$book._id
    $imageListUrl = "$CatalogBaseUrl/api/catalog/books/$bookId/images"

    $headers = @{}
    if ($BearerToken) {
        $headers["Authorization"] = "Bearer $BearerToken"
    }

    $hasImages = $false
    try {
        $existing = Invoke-RestMethod -Method Get -Uri $imageListUrl -Headers $headers
        if ($existing -is [System.Array]) {
            $hasImages = $existing.Count -gt 0
        } elseif ($null -ne $existing) {
            $hasImages = $true
        }
    }
    catch {
        Write-Warning "Could not query existing images for ${bookId}: $($_.Exception.Message)"
    }

    if ($hasImages) {
        Write-Host "Skipping image upload for $bookId (already present)."
        continue
    }

    $downloadPath = Join-Path $tempDir ("$bookId-cover.jpg")
    & curl.exe -sL ([string]$book.sourceImageUrl) -o $downloadPath
    if ($LASTEXITCODE -ne 0 -or -not (Test-Path $downloadPath) -or (Get-Item $downloadPath).Length -eq 0) {
        Write-Warning "Failed to download cover image for $bookId from $($book.sourceImageUrl)"
        continue
    }

    $uploadUrl = "$CatalogBaseUrl/api/catalog/books/$bookId/images"
    $curlArgs = @("-sS", "-X", "POST", $uploadUrl, "-F", "images=@$downloadPath;type=image/jpeg")
    if ($BearerToken) {
        $curlArgs = @("-sS", "-X", "POST", $uploadUrl, "-H", "Authorization: Bearer $BearerToken", "-F", "images=@$downloadPath;type=image/jpeg")
    }

    $uploadResponse = & curl.exe @curlArgs
    if ($LASTEXITCODE -eq 0) {
        $uploadResponse | Out-Host
        Write-Host "Uploaded cover to MinIO via catalog-service for $bookId"
    } else {
        Write-Warning "Failed to upload cover for $bookId via catalog-service"
    }
}

Write-Host "Seeding complete."
