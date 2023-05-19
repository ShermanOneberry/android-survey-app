migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("22tej05klv78p5h")

  collection.indexes = [
    "CREATE UNIQUE INDEX `idx_4H5YQkF` ON `surveyResults` (`surveyRequest`)"
  ]

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("22tej05klv78p5h")

  collection.indexes = [
    "CREATE INDEX `idx_4H5YQkF` ON `surveyResults` (`surveyRequest`)"
  ]

  return dao.saveCollection(collection)
})
