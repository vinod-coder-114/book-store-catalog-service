// MongoDB seed script for catalog-service books collection
// Run with: mongosh "mongodb://localhost:27017/catalog_db" ./mongo-book-seed.js

use('catalog_db');

// Optional reset for repeatable local testing
db.books.deleteMany({});

// Optional indexes aligned with Book entity text fields
db.books.createIndex({ title: "text", author: "text" });

db.books.insertMany([
  {
    _id: "bk_10293",
    title: "Atomic Habits",
    author: "James Clear",
    genre: "Self Help",
    format: "Paperback",
    pricing: {
      currency: "INR",
      salePrice: 399,
      listPrice: 499
    },
    rating: {
      average: 4.7,
      count: 12933
    },
    images: {
      front: {
        card: "https://cdn.example.com/books/bk_10293/front-card.avif",
        thumb: "https://cdn.example.com/books/bk_10293/front-thumb.avif"
      },
      back: {
        card: "https://cdn.example.com/books/bk_10293/back-card.avif",
        thumb: "https://cdn.example.com/books/bk_10293/back-thumb.avif"
      },
      aspectRatio: 0.75,
      blurHash: "LKO2?U%2Tw=w]~RBVZRi};RPxuwH"
    }
  },
  {
    _id: "bk_10411",
    title: "Deep Work",
    author: "Cal Newport",
    genre: "Productivity",
    format: "Hardcover",
    pricing: {
      currency: "INR",
      salePrice: 549,
      listPrice: 699
    },
    rating: {
      average: 4.6,
      count: 8451
    },
    images: {
      front: {
        card: "https://cdn.example.com/books/bk_10411/front-card.avif",
        thumb: "https://cdn.example.com/books/bk_10411/front-thumb.avif"
      },
      back: {
        card: "https://cdn.example.com/books/bk_10411/back-card.avif",
        thumb: "https://cdn.example.com/books/bk_10411/back-thumb.avif"
      },
      aspectRatio: 0.75,
      blurHash: "LEHV6nWB2yk8pyo0adR*.7kCMdnj"
    }
  },
  {
    _id: "bk_10754",
    title: "The Psychology of Money",
    author: "Morgan Housel",
    genre: "Finance",
    format: "Paperback",
    pricing: {
      currency: "INR",
      salePrice: 379,
      listPrice: 450
    },
    rating: {
      average: 4.8,
      count: 21445
    },
    images: {
      front: {
        card: "https://cdn.example.com/books/bk_10754/front-card.avif",
        thumb: "https://cdn.example.com/books/bk_10754/front-thumb.avif"
      },
      back: {
        card: "https://cdn.example.com/books/bk_10754/back-card.avif",
        thumb: "https://cdn.example.com/books/bk_10754/back-thumb.avif"
      },
      aspectRatio: 0.75,
      blurHash: "L9AS#2x]00NG~oRjIURj00x]M{of"
    }
  },
  {
    _id: "bk_10902",
    title: "Clean Code",
    author: "Robert C. Martin",
    genre: "Programming",
    format: "Paperback",
    pricing: {
      currency: "INR",
      salePrice: 699,
      listPrice: 899
    },
    rating: {
      average: 4.5,
      count: 17602
    },
    images: {
      front: {
        card: "https://cdn.example.com/books/bk_10902/front-card.avif",
        thumb: "https://cdn.example.com/books/bk_10902/front-thumb.avif"
      },
      back: {
        card: "https://cdn.example.com/books/bk_10902/back-card.avif",
        thumb: "https://cdn.example.com/books/bk_10902/back-thumb.avif"
      },
      aspectRatio: 0.75,
      blurHash: "LFI5YQ9F00Rj_3t7M{Rj00RjRjRj"
    }
  },
  {
    _id: "bk_11037",
    title: "Ikigai",
    author: "Hector Garcia",
    genre: "Wellness",
    format: "Paperback",
    pricing: {
      currency: "INR",
      salePrice: 299,
      listPrice: 399
    },
    rating: {
      average: 4.4,
      count: 9820
    },
    images: {
      front: {
        card: "https://cdn.example.com/books/bk_11037/front-card.avif",
        thumb: "https://cdn.example.com/books/bk_11037/front-thumb.avif"
      },
      back: {
        card: "https://cdn.example.com/books/bk_11037/back-card.avif",
        thumb: "https://cdn.example.com/books/bk_11037/back-thumb.avif"
      },
      aspectRatio: 0.75,
      blurHash: "L47B6#009F~qM{M{RjM{00M{RjM{"
    }
  }
]);

// Quick verification queries
db.books.countDocuments({});
db.books.find({}, { _id: 1, title: 1, author: 1, genre: 1, format: 1 }).sort({ _id: 1 });

