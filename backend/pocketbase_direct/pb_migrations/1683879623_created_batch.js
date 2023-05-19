migrate((db) => {
  const collection = new Collection({
    "id": "rvkulj5snpgkvf3",
    "created": "2023-05-12 08:20:23.358Z",
    "updated": "2023-05-12 08:20:23.358Z",
    "name": "batch",
    "type": "base",
    "system": false,
    "schema": [
      {
        "system": false,
        "id": "mob8j0sf",
        "name": "number",
        "type": "number",
        "required": false,
        "unique": false,
        "options": {
          "min": null,
          "max": null
        }
      }
    ],
    "indexes": [],
    "listRule": null,
    "viewRule": null,
    "createRule": null,
    "updateRule": null,
    "deleteRule": null,
    "options": {}
  });

  return Dao(db).saveCollection(collection);
}, (db) => {
  const dao = new Dao(db);
  const collection = dao.findCollectionByNameOrId("rvkulj5snpgkvf3");

  return dao.deleteCollection(collection);
})
