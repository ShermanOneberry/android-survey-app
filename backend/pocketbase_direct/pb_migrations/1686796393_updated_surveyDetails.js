migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("9jhzcgy0yo8egqd")

  collection.createRule = "@request.auth.id ?= @collection.bots.id"
  collection.updateRule = "@request.auth.id ?= @collection.bots.id"

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("9jhzcgy0yo8egqd")

  collection.createRule = null
  collection.updateRule = null

  return dao.saveCollection(collection)
})
