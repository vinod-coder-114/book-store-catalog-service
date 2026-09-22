use('catalog_db');

const books = [
    {
        "_id":  "bk_mx_001",
        "title":  "Alice\u0027s Adventures in Wonderland",
        "author":  "Lewis Carroll",
        "genre":  "Fantasy",
        "format":  "Paperback",
        "pricing":  {
                        "currency":  "INR",
                        "salePrice":  675,
                        "listPrice":  822
                    },
        "rating":  {
                       "average":  4,
                       "count":  973
                   },
        "sourceImageUrl":  "https://covers.openlibrary.org/b/id/10527843-L.jpg"
    },
    {
        "_id":  "bk_mx_002",
        "title":  "Frankenstein; or, The Modern Prometheus",
        "author":  "Mary Shelley",
        "genre":  "Science Fiction",
        "format":  "Paperback",
        "pricing":  {
                        "currency":  "INR",
                        "salePrice":  662,
                        "listPrice":  807
                    },
        "rating":  {
                       "average":  4.1,
                       "count":  1146
                   },
        "sourceImageUrl":  "https://covers.openlibrary.org/b/id/12356249-L.jpg"
    },
    {
        "_id":  "bk_mx_003",
        "title":  "Bible",
        "author":  "Bible",
        "genre":  "History",
        "format":  "Paperback",
        "pricing":  {
                        "currency":  "INR",
                        "salePrice":  643,
                        "listPrice":  783
                    },
        "rating":  {
                       "average":  4.2,
                       "count":  1319
                   },
        "sourceImageUrl":  "https://covers.openlibrary.org/b/id/12707846-L.jpg"
    },
    {
        "_id":  "bk_mx_004",
        "title":  "The Art of War",
        "author":  "(Sun Tzu)",
        "genre":  "Business",
        "format":  "Paperback",
        "pricing":  {
                        "currency":  "INR",
                        "salePrice":  300,
                        "listPrice":  365
                    },
        "rating":  {
                       "average":  4.3,
                       "count":  1492
                   },
        "sourceImageUrl":  "https://covers.openlibrary.org/b/id/4849549-L.jpg"
    },
    {
        "_id":  "bk_mx_005",
        "title":  "Jonathan Livingston Le Goeland",
        "author":  "Richard Bach",
        "genre":  "Self Help",
        "format":  "Paperback",
        "pricing":  {
                        "currency":  "INR",
                        "salePrice":  513,
                        "listPrice":  625
                    },
        "rating":  {
                       "average":  4.4,
                       "count":  1665
                   },
        "sourceImageUrl":  "https://covers.openlibrary.org/b/id/6522230-L.jpg"
    },
    {
        "_id":  "bk_mx_006",
        "title":  "The Hound of the Baskervilles",
        "author":  "Arthur Conan Doyle",
        "genre":  "Mystery",
        "format":  "Paperback",
        "pricing":  {
                        "currency":  "INR",
                        "salePrice":  442,
                        "listPrice":  538
                    },
        "rating":  {
                       "average":  4.5,
                       "count":  1838
                   },
        "sourceImageUrl":  "https://covers.openlibrary.org/b/id/8063264-L.jpg"
    },
    {
        "_id":  "bk_mx_007",
        "title":  "Gulliver\u0027s Travels",
        "author":  "Jonathan Swift",
        "genre":  "Biography",
        "format":  "Paperback",
        "pricing":  {
                        "currency":  "INR",
                        "salePrice":  317,
                        "listPrice":  386
                    },
        "rating":  {
                       "average":  4.6,
                       "count":  2011
                   },
        "sourceImageUrl":  "https://covers.openlibrary.org/b/id/12717083-L.jpg"
    },
    {
        "_id":  "bk_mx_008",
        "title":  "Reconfigurable Processor Array A Bit Sliced Parallel Computer (USA)",
        "author":  "A. Rushton",
        "genre":  "Programming",
        "format":  "Paperback",
        "pricing":  {
                        "currency":  "INR",
                        "salePrice":  647,
                        "listPrice":  789
                    },
        "rating":  {
                       "average":  4.7,
                       "count":  2184
                   },
        "sourceImageUrl":  "https://covers.openlibrary.org/b/id/8237007-L.jpg"
    },
    {
        "_id":  "bk_mx_009",
        "title":  "The Prince",
        "author":  "Niccol Machiavelli",
        "genre":  "Psychology",
        "format":  "Paperback",
        "pricing":  {
                        "currency":  "INR",
                        "salePrice":  352,
                        "listPrice":  429
                    },
        "rating":  {
                       "average":  3.9,
                       "count":  2357
                   },
        "sourceImageUrl":  "https://covers.openlibrary.org/b/id/12726168-L.jpg"
    },
    {
        "_id":  "bk_mx_010",
        "title":  "Hamlet",
        "author":  "William Shakespeare",
        "genre":  "Philosophy",
        "format":  "Paperback",
        "pricing":  {
                        "currency":  "INR",
                        "salePrice":  775,
                        "listPrice":  944
                    },
        "rating":  {
                       "average":  4,
                       "count":  2530
                   },
        "sourceImageUrl":  "https://covers.openlibrary.org/b/id/8281954-L.jpg"
    }
];

const operations = books.map((b) => ({
  updateOne: {
    filter: { _id: b._id },
    update: {
      $set: {
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
