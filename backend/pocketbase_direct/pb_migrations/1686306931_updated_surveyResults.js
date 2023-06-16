migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("22tej05klv78p5h")

  collection.listRule = "@request.auth.id = assignedUser.id"

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("22tej05klv78p5h")

  collection.listRule = null

  return dao.saveCollection(collection)
})
