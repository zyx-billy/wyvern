/* Encodes test files into binary protobuf format
 *
 * Test files should be provided with a "./" prefix
 *
 * Example Usage
 * $ node nominal_wyvern/tests/encoder.js ./basic.js
 */

const protobuf = require("protobufjs");
const fs = require("fs");

if (process.argv.length < 3)
  throw Error("please provide test file as argument");

var testFile = process.argv[2];
var testFileBaseName = testFile.slice(0, testFile.lastIndexOf("."))
var testModule = require(testFile);

var msg = testModule.msg;
console.log(msg)

var root = protobuf.loadSync(process.env.WYVERN_HOME + "/backend/nw-bytecode.proto");
const Bytecode = root.lookupType("nominalWyvern.Bytecode");
var errMsg = Bytecode.verify(msg);
if (errMsg)
  throw Error(errMsg);

var encodedMsg = Bytecode.fromObject(msg);
console.log(encodedMsg)
var buffer = Bytecode.encode(encodedMsg).finish();
fs.writeFileSync(testFileBaseName + ".nwb", new Buffer(buffer));
