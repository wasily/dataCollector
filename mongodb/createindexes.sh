mongo --eval "db.content_releases.createIndex({ \"regTime\" : 1})" dev
mongo --eval "db.content_releases.createIndex({ \"contentType\" : 1})" dev
mongo --eval "db.content_releases.createIndex({\"trackerId\" : 1, \"regTime\" : 1}, { unique : true })" dev
mongo --eval "db.movies.createIndex({\"imdbId\" : 1}, { unique : true })" dev
mongo --eval "db.movies.createIndex({\"primaryTitle\" : 1})" dev
mongo --eval "db.series.createIndex({\"imdbId\" : 1}, { unique : true })" dev
mongo --eval "db.series.createIndex({\"primaryTitle\" : 1})" dev
mongo --eval "db.subscriptions.createIndex({\"imdbId\" : 1, \"userEmail\" : 1}, { unique : true })" dev
