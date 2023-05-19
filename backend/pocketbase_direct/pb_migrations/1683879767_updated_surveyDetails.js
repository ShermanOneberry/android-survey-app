migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("9jhzcgy0yo8egqd")

  collection.indexes = [
    "CREATE UNIQUE INDEX `idx_J7bB7iQ` ON `surveyDetails` (\n  `batchNumber`,\n  `batchID`\n)"
  ]

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("9jhzcgy0yo8egqd")

  collection.indexes = []

  return dao.saveCollection(collection)
})
