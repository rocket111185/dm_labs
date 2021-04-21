'use strict';

const context = document.getElementById('canvas').getContext('2d');
const button = document.getElementById('bt');
button.innerText = 'Наступний крок';

const graphCenterX = 400;
const graphCenterY = 400;
const treeGraphCenterX = 1200;
const treeGraphCenterY = 400;
const radius = 30;


const coordX = [];
const coordY = [];
const treeX = [];
const treeY = [];

const initialMatrix = [
  [1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0],
  [1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0],
  [1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 1],
  [0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1],
  [1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 0],
  [0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0],
  [0, 1, 0, 1, 0, 1, 1, 0, 1, 1, 1],
  [0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0]
];

const printMatrix = matrix => {
  for (const row of matrix) {
    console.log(row.join('\t'));
  }
};

const makeUnoriented = matrix => {
  for (let i = 0; i < matrix.length; i++) {
    for (let j = 0; j < matrix.length; j++) {
      if (matrix[j][i])
        matrix[i][j] = 1;
    }
  }
  return matrix;
};

console.log('Oriented matrix');
printMatrix(initialMatrix);
const matrix = makeUnoriented(initialMatrix);
console.log('Unoriented matrix');
printMatrix(matrix);

const wMatrix = [
  [0,  0,  46, 29, 14,  0,  24, 42, 56,  0,  57],
  [0,  0,  0,  0,  0,   0,  81, 0,  0,   64, 19],
  [46, 0,  0,  5,  13,  18, 13, 65, 0,   54, 0],
  [29, 0,  5,  0,  6,   54, 10, 0,  0,   1,  0],
  [14, 0,  13, 6,  0,   42, 0,  35, 100, 0,  80],
  [0,  0,  18, 54, 42,  0,  10, 0,  40,  69, 45],
  [24, 81, 13, 10, 0,   10, 0,  11, 42,  85, 0],
  [42, 0,  65, 0,  35,  0,  11, 0,  36,  0,  92],
  [56, 0,  0,  0,  100, 40, 42, 36, 0,   99, 0],
  [0,  64, 54, 1,  0,   69, 85, 0,  99,  0,  48],
  [57, 19, 0,  0,  80,  45, 0,  92, 0,   48, 0]
];

console.log('Matrix of weights');
printMatrix(wMatrix);
const vertexCount = matrix.length;
const tree = Array(vertexCount ** 2).fill(0);

/*
 * Use self-calling function to get the result at once
 * The local function returns the object, that contains:
 * key: weight of an edge;
 * value: array of vertexes, linked by the edge.
 * Keys are sorted in ascending order.
 */

// TODO: Contain edges in array, not object.
// Look for the edges of the same weight, if not:
// up minWeight to MAX_SAFE_INTEGER again.
const edges = (() => {
  let minWeight = Number.MAX_SAFE_INTEGER;
  const edgesList = [];
  const weightList = [];
  while (true) {
    minWeight = Number.MAX_SAFE_INTEGER;
    let from, to;
    for (let i = 0; i < vertexCount; i++) {
      for (let j = 0; j < vertexCount; j++) {
        const currentW = wMatrix[i][j];
        if (currentW > 0 && currentW <= minWeight &&
          (!edgesList.includes(currentW) ||
          edgesList[edgesList.length - 1] === currentW)) {
          let proper = true;
          edgesList.forEach(edge => {
            if (edge.includes(i) && edge.includes(j))
              proper = false;
          });
          if (proper) {
            from = i;
            to = j;
            minWeight = currentW;
          }
        }
      }
    }
    if (minWeight === Number.MAX_SAFE_INTEGER) break;
    edgesList.push([from, to]);
    weightList.push(minWeight);
  }
  return edgesList;
})();

const properEdges = (() => {
  let unvisited = Array(vertexCount)
    .fill(0)
    .map((el, i) => i);
  unvisited.shift();
  const res = [];
  while (unvisited.length) {
    let weight = Number.MAX_SAFE_INTEGER;
    let activeNode, nextNode;
    for (let i = 0; i < vertexCount; i++) {
      for (let j = 0; j < vertexCount; j++) {
        if (i !== j &&
              wMatrix[i][j] < weight &&
              wMatrix[i][j] > 0 &&
              !unvisited.includes(i) &&
              unvisited.includes(j)) {
          activeNode = i;
          nextNode = j;
          weight = wMatrix[i][j];
        }
      }
    }
    res.push([activeNode, nextNode]);
    unvisited = unvisited.filter(el => !(el === activeNode || el === nextNode));
  }
  return res;
})();

const drawVertex = (x, y, text) => {
  context.beginPath();
  context.arc(x, y, radius, 0, 2 * Math.PI);
  context.stroke();
  context.font = '24px serif';
  context.fillText(text, x - 6, y + 8);
};


const getAngle = (x1, y1, x2, y2) => {
  const angle = [];
  const path = Math.sqrt((x2 - x1) ** 2 + (y2 - y1) ** 2);
  angle[0] = (y2 - y1) / path;
  angle[1] = (x2 - x1) / path;
  return angle;
};

const lineDrawer = (x1, y1, x2, y2, weight, type) => {
  context.beginPath();
  if (type === 'evaluating') context.strokeStyle = '#55DBBE';
  else if (type === 'choosen') context.strokeStyle = '#CD3F45';
  if (x1 === x2 && y1 === y2) {
    context.moveTo(x1, y2 + radius);
    context.lineTo(x1 + radius, y2 + radius);
    context.lineTo(x1 + radius, y2);
    context.stroke();
  }
  const angle = getAngle(x1, y1, x2, y2);
  const sin = angle[0];
  const cos = angle[1];
  x1 += radius * cos;
  y1 += radius * sin;
  x2 -= radius * cos;
  y2 -= radius * sin;
  context.moveTo(x1, y1);
  context.lineTo(x2, y2);
  context.stroke();
  const pointSize = radius / 2;
  const tx = x2 - 2 * pointSize * cos;
  const ty = y2 - 2 * pointSize * sin;
  context.strokeStyle = 'black';
  if (type === 'none') {
    context.font = '12px mono';
    context.fillText(weight, tx, ty);
  }
};



let vertex1 = null;
let vertex2 = null;

const findProperEdge = () => {
  if (vertex1 !== null) {
    let properEdge = false;
    properEdges.forEach(edge => {
      if (edge.includes(vertex1) && edge.includes(vertex2))
        properEdge = true;
    });
    if (properEdge) {
      lineDrawer(coordX[vertex1], coordY[vertex1],
        coordX[vertex2], coordY[vertex2],
        wMatrix[vertex1][vertex2], 'choosen');
      lineDrawer(treeX[vertex1], treeY[vertex1],
        treeX[vertex2], treeY[vertex2],
        wMatrix[vertex1][vertex2], 'none');
      tree[vertex1 * vertexCount + vertex2] = 1;
      tree[vertex2 * vertexCount + vertex1] = 1;
    } else if (vertex1 !== vertex2) {
      lineDrawer(coordX[vertex1], coordY[vertex1],
        coordX[vertex2], coordY[vertex2],
        wMatrix[vertex1][vertex2], 'non');
      tree[vertex1 * vertexCount + vertex2] = 1;
      tree[vertex2 * vertexCount + vertex1] = 1;
    }
  }
  if (edges.length) {
    const edgeArr = edges.shift();
    vertex1 = edgeArr[0];
    vertex2 = edgeArr[1];
    lineDrawer(coordX[vertex1], coordY[vertex1],
      coordX[vertex2], coordY[vertex2],
      wMatrix[vertex1][vertex2], 'evaluating');
  }
};


/*
 * 0 - find proper egde
 * 1 - all egdes are scanned, end kruskalAlg()
 */
let task = 0;

const kruskalAlg = () => {
  if (!task) {
    if (!Object.keys(edges).length)
      task = 1;
    findProperEdge();
  } else {
    console.log('Tree matrix');
    for (let i = 0; i < vertexCount; i++) {
      console.log(tree
        .slice(i * vertexCount, (i + 1) * vertexCount)
        .join('\t'));
    }
    button.removeEventListener('click', kruskalAlg);
  }
};

button.addEventListener('click', kruskalAlg);

for (let i = 0; i < vertexCount; i++) {
  const x = (graphCenterX + 300 * Math.cos(2 * Math.PI * i / vertexCount));
  const y = (graphCenterY + 300 * Math.sin(2 * Math.PI * i / vertexCount));
  coordX.push(x);
  coordY.push(y);
  drawVertex(x, y, i + 1);
}

for (let i = 0; i < vertexCount; i++) {
  const x = (treeGraphCenterX + 300 * Math.cos(2 * Math.PI * i / vertexCount));
  const y = (treeGraphCenterY + 300 * Math.sin(2 * Math.PI * i / vertexCount));
  treeX.push(x);
  treeY.push(y);
  drawVertex(x, y, i + 1);
}

const tweens = [];
let tween = false;

for (let i = 0; i < vertexCount; i++) {
  for (let j = 0; j < vertexCount; j++) {
    if (matrix[i][j]) {
      if (matrix[j][i] && !tweens.includes(`${i}${j}`)) {
        tween = true;
        tweens.push(`${j}${i}`);
      }
      if (!tween)
        lineDrawer(coordX[i], coordY[i], coordX[j], coordY[j],
          wMatrix[i][j], 'none');
    }
    tween = false;
  }
}
