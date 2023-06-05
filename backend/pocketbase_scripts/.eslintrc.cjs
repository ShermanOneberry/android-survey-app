module.exports = {
    root: true,
    parser: '@typescript-eslint/parser',
    plugins: ['@typescript-eslint'],
    extends: [
      'eslint:recommended', 
      'plugin:@typescript-eslint/recommended', 
      'plugin:@typescript-eslint/recommended-requiring-type-checking',
    ],
    parserOptions: {
      tsconfigRootDir: __dirname, //Get vscode eslint to work with non-root eslintrc file
      project: './tsconfig.json',
      sorucetype: 'module',
    },
    ignorePatterns: [
      ".eslintrc.cjs", 
    ],
    
  };