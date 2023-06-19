migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("wvpld86o2bqxwsf")

  collection.name = "batches"

  // remove
  collection.schema.removeField("eirzyvtr")

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "fmx2u7km",
    "name": "batchNumber",
    "type": "number",
    "required": false,
    "unique": false,
    "options": {
      "min": null,
      "max": null
    }
  }))

  return dao.saveCollection(collection)
}, (db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("wvpld86o2bqxwsf")

  collection.name = "latest_batch_changes"

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "eirzyvtr",
    "name": "batchNumber",
    "type": "number",
    "required": false,
    "unique": false,
    "options": {
      "min": null,
      "max": null
    }
  }))

  // remove
  collection.schema.removeField("fmx2u7km")

  return dao.saveCollection(collection)
})
