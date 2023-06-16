migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("22tej05klv78p5h")

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "4hbougp8",
    "name": "additionalImage",
    "type": "file",
    "required": false,
    "unique": false,
    "options": {
      "maxSelect": 1,
      "maxSize": 5242880,
      "mimeTypes": [],
      "thumbs": [],
      "protected": true
    }
  }))

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("22tej05klv78p5h")

  // remove
  collection.schema.removeField("4hbougp8")

  return dao.saveCollection(collection)
})
