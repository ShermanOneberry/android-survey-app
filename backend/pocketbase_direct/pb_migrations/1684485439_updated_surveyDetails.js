migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("9jhzcgy0yo8egqd")

  // remove
  collection.schema.removeField("vysrdaz0")

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("9jhzcgy0yo8egqd")

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "vysrdaz0",
    "name": "assignedUser",
    "type": "relation",
    "required": false,
    "unique": false,
    "options": {
      "collectionId": "_pb_users_auth_",
      "cascadeDelete": false,
      "minSelect": null,
      "maxSelect": 1,
      "displayFields": []
    }
  }))

  return dao.saveCollection(collection)
})
