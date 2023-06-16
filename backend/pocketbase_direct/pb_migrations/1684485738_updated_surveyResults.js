migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("22tej05klv78p5h")

  collection.createRule = "@request.auth.id = assignedUser.id"

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "9ihtlclg",
    "name": "assignedUser",
    "type": "relation",
    "required": true,
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
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("22tej05klv78p5h")

  collection.createRule = null

  // remove
  collection.schema.removeField("9ihtlclg")

  return dao.saveCollection(collection)
})
