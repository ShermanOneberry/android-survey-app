migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("22tej05klv78p5h")

  collection.updateRule = "@request.auth.id = assignedUser.id && @request.auth.id = @request.data.assignedUser.id && @request.auth.id ?= @collection.users.id"

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("22tej05klv78p5h")

  collection.updateRule = null

  return dao.saveCollection(collection)
})
