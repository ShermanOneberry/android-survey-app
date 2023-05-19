migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("22tej05klv78p5h")

  // remove
  collection.schema.removeField("jre5e3cz")

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "hcaimpsw",
    "name": "surveyRequest",
    "type": "relation",
    "required": true,
    "unique": false,
    "options": {
      "collectionId": "9jhzcgy0yo8egqd",
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

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "jre5e3cz",
    "name": "userAssigned",
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

  // remove
  collection.schema.removeField("hcaimpsw")

  return dao.saveCollection(collection)
})
