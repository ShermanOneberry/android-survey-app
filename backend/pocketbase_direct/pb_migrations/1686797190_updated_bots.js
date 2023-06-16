migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("7zifyg3o2gz3hdo")

  collection.listRule = null

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("7zifyg3o2gz3hdo")

  collection.listRule = ""

  return dao.saveCollection(collection)
})
