migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("9jhzcgy0yo8egqd")

  collection.viewRule = "@request.auth.id != \"\""

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("9jhzcgy0yo8egqd")

  collection.viewRule = null

  return dao.saveCollection(collection)
})
