migrate((db) => {
  const collection = new Collection({
    "id": "yozk2dezo7gzwjs",
    "created": "2023-05-12 06:45:09.425Z",
    "updated": "2023-05-12 06:45:09.425Z",
    "name": "surveyResults",
    "type": "base",
    "system": false,
    "schema": [
      {
        "system": false,
        "id": "a4lwk6kg",
        "name": "testtt",
        "type": "editor",
        "required": false,
        "unique": false,
        "options": {}
      },
      {
        "system": false,
        "id": "peh8aibg",
        "name": "test",
        "type": "text",
        "required": false,
        "unique": false,
        "options": {
          "min": null,
          "max": null,
          "pattern": ""
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
  const collection = dao.findCollectionByNameOrId("yozk2dezo7gzwjs");

  return dao.deleteCollection(collection);
})
