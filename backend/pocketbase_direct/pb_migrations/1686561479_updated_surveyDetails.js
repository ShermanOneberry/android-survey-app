migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("9jhzcgy0yo8egqd")

  collection.listRule = "@request.auth.id != \"\""
  collection.createRule = ""

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("9jhzcgy0yo8egqd")

  collection.listRule = null
  collection.createRule = null

  return dao.saveCollection(collection)
})
