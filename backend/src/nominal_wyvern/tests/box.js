/* 

name Box {b =>
  type T <= TOP
  val x : b.T
}

let a = new Box {b =>
  type T = Int
  val x : Int = 2
} in a.x

*/

const enums = require("./enums.js")

module.exports.msg = {
  topLevelDecls: [
    {
      namedTypeDecl: {
        name: "Box",
        selfName: "b",
        members: [
          {
            typeMemberDecl: {
              name: "T",
              direction: enums.bound.UPPER,
              bound: {
                baseType: {
                  builtinType: enums.builtinType.TOP
                },
                refinement: {
                  typeDecls: []
                }
              }
            }
          },
          {
            fieldMemberDecl: {
              name: "x",
              type: {
                baseType: {
                  pdType: {
                    path: {
                      variable: "b"
                    },
                    label: "T"
                  }
                },
                refinement: {
                  typeDecls: []
                }
              }
            }
          }
        ]
      }
    }
  ],
  main: {
    path: {
      variable: "x"
    }
  }
};