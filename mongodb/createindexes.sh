mongo --eval "db.content_releases.createIndex({ \"regTime\" : 1})" dev
mongo --eval "db.content_releases.createIndex({ \"contentType\" : 1})" dev
mongo --eval "db.content_releases.createIndex({\"trackerId\" : 1, \"regTime\" : 1}, { unique : true })" dev
mongo --eval "db.movies.createIndex({\"imdbId\" : 1}, { unique : true })" dev
mongo --eval "db.movies.createIndex({\"primaryTitle\" : 1})" dev
mongo --eval "db.series.createIndex({\"imdbId\" : 1}, { unique : true })" dev
mongo --eval "db.series.createIndex({\"primaryTitle\" : 1})" dev
mongo --eval "db.subscriptions.createIndex({\"imdbId\" : 1, \"userEmail\" : 1}, { unique : true })" dev
mongo --eval "db.subscriptions.insert([
    {    
    \"imdbId\" : \"tt4643084\",
    \"contentType\" : \"series\",
    \"contentTitle\" : \"Counterpart\",
    \"userEmail\" : \"the-useless-box@mail.ru\",
    \"lastUpdateTime\" : ISODate(\"2019-01-23T11:27:50.951Z\")
    },
    {
    \"imdbId\" : \"tt9243946\",
    \"contentType\" : \"movie\",
    \"contentTitle\" : \"El Camino: A Breaking Bad Movie\",
    \"userEmail\" : \"the-useless-box@mail.ru\",
    \"lastUpdateTime\" : ISODate(\"2018-10-23T11:27:51.221Z\")
    }])" dev
mongo --eval "db.events.createIndex({ \"eventTime\" : -1})" dev