migrate((db) => {
  const dao = new Dao(db)
  const collection = dao.findCollectionByNameOrId("wvpld86o2bqxwsf")

  collection.listRule = "@request.auth.id ?= @collection.bots.id"

  // remove
  collection.schema.removeField("fmx2u7km")

  // add
  collection.schema.addField(new SchemaField({
    "system": false,
    "id": "0g960nju",
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

  collection.listRule = null

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

  // remove
  collection.schema.removeField("0g960nju")

  return dao.saveCollection(collection)
})
