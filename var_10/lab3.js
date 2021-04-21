'use strict';

const context = document.getElementById('canvas').getContext('2d');

const graphCenterX = 700;
const graphCenterY = 460;
const radius = 30;

const coordX = [];
const coordY = [];
const vertexList = [];

const matrix = [
  [1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1],
  [0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0],
  [1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1],
  [0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0],
  [0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1],
  [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0],
  [0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0],
  [1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0],
  [1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0],
  [0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1]
];

const vertexCount = matrix.length;
const isCondensed = confirm('Конденсований граф?');

const Vertex = label => ({
  label,
  positiveDegree: 0,
  negativeDegree: 0,
  degree: 0,
  increasePositiveDegree() {
    this.positiveDegree++;
    this.degree++;
  },
  increaseNegativeDegree() {
    this.negativeDegree++;
    this.degree++;
  }
});

const prepareMatrix = size => Array(size).fill(Array(size).fill(0));

const boolConvert = matrix =>
  matrix.map(row => row.map(el => (el ? 1 : 0)));

const copyMatrix = matrix => JSON.parse(JSON.stringify(matrix));

const matrixMultiplication = (one, other = one) => {
  const result = prepareMatrix(one.length);
  return result
    .map((row, i) =>
      row.map((val, j) =>
        one[i].reduce((sum, el, k) => sum + (el * other[k][j]), 0)));
};

const getMatrixOfWays = wayLength => {
  const result = prepareMatrix(vertexCount);
  let previous = copyMatrix(matrix);
  let current = copyMatrix(matrix);
  for (let i = 1; i < wayLength; i++) {
    previous = copyMatrix(current);
    current = matrixMultiplication(current, previous);
  }
  return result.map((row, i) => row.map((val, j) =>
    ((previous[i][j] === 0 && current[i][j] > 0) ? 1 : 0)));
};

const getWays = wayLength => {
  let result = [];
  for (let step = 0; step < wayLength; step++) {
    const currentResult = [];
    for (let i = 0; i < vertexCount; i++) {
      for (let j = 0; j < vertexCount; j++) {
        if (i === j) continue;
        if (matrix[i][j]) {
          if (!step) {
            currentResult.push([i, j]);
          } else {
            result.forEach(row => {
              if (row[row.length - 1] === i)
                currentResult.push([...row, j]);
            });
          }
        }
      }
    }
    result = copyMatrix(currentResult);
  }
  result = copyMatrix(result.map(row => row.map(val => ++val)));
  return result;
};

const getReachable = () => {
  let result = prepareMatrix(vertexCount);
  const list = [];
  let current = copyMatrix(matrix);
  for (let i = 0; i < vertexCount; i++) {
    list[i] = current;
    current = boolConvert(matrixMultiplication(current));
  }
  list.forEach(listMatrix => {
    result = result.map((row, i) => row.map((val, j) => listMatrix[i][j]));
  });
  return boolConvert(result);
};

const getStrongLinkedMatrix = () => {
  const result = prepareMatrix(vertexCount);
  const reach = getReachable();
  return result
    .map((row, i) =>
      row.map((val, j) => ((reach[i][j] > 0 && reach[j][i] > 0) ? 1 : 0)));
};

const maxValue = init => {
  let max = init[0][0];
  init.forEach(row => {
    row.forEach(val => {
      if (val > max) max = val;
    });
  });
  return max;
};

const getStrongLinked = () => {
  const initial = getStrongLinkedMatrix();
  const linked = matrixMultiplication(initial);
  const distinctElements = [];
  let currentGroup = [];
  const result = [];

  let input = 0;
  let output = 0;
  for (let i = 0; i < vertexCount; i++) {
    for (let j = 0; j < vertexCount; j++) {
      if (matrix[i][j] > 0) output++;
      if (matrix[j][i] > 0) input++;
      if (input > 0 && output > 0) break;
    }
    if (output === 0 || input === 0) {
      distinctElements.push(i);
      result.push([i]);
    }
    input = 0;
    output = 0;
  }

  for (let i = 1; i <= maxValue(linked); i++) {
    for (let j = 0; j < vertexCount; j++) {
      for (let k = 0; k < vertexCount; k++) {
        if (linked[j][k] === linked[k][j] && linked[j][k] === i) {
          if (!distinctElements.includes(j) && !distinctElements.includes(k)) {
            if (!currentGroup.includes(j)) currentGroup.push(j);
            if (!currentGroup.includes(k)) currentGroup.push(k);
          }
        }
      }
    }
    if (currentGroup.length)
      result.push(currentGroup);
    currentGroup = [];
  }
  return result;
};

const getCondensedMatrix = () => {
  const strongLinked = getStrongLinked();
  const result = prepareMatrix(strongLinked.length);
  return result.map((row, i) => row.map((el, j) => {
    if (i === j) return 0;
    const inputRow = strongLinked[i];
    const outputRow = strongLinked[j];
    for (const input of inputRow) {
      for (const output of outputRow) {
        if (matrix[input][output] > 0) {
          return 1;
        }
      }
    }
    return 0;
  }));
};


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

const lineDrawer = (x1, y1, x2, y2, tween) => {
  context.beginPath();
  if (x1 === x2 && y1 === y2) {
    context.moveTo(x1, y2 + radius);
    context.lineTo(x1 + radius, y2 + radius);
    context.lineTo(x1 + radius, y2);
    context.stroke();
  }
  let angle = getAngle(x1, y1, x2, y2);
  let sin = angle[0];
  let cos = angle[1];
  x1 += radius * cos;
  y1 += radius * sin;
  x2 -= radius * cos;
  y2 -= radius * sin;
  context.moveTo(x1, y1);
  if (tween) {
    const midX = (x1 + x2) / 2 + 15;
    const midY = (y1 + y2) / 2 + 20;
    angle = getAngle(midX, midY, x2, y2);
    sin = angle[0];
    cos = angle[1];
    context.lineTo(midX, midY);
  }
  context.lineTo(x2, y2);
  context.stroke();
  const pointSize = radius / 6;
  const cx1 = x2 - 2 * pointSize * cos - pointSize * sin;
  const cy1 = y2 - 2 * pointSize * sin + pointSize * cos;
  const cx2 = x2 - 2 * pointSize * cos + pointSize * sin;
  const cy2 = y2 - 2 * pointSize * sin - pointSize * cos;
  context.moveTo(x2, y2);
  context.lineTo(cx1, cy1);
  context.lineTo(cx2, cy2);
  context.lineTo(x2, y2);
  context.stroke();
};


const strongLinked = getStrongLinked();
const condensedMatrix = getCondensedMatrix();

// Draw vertexes on canvas
if (!isCondensed) {
  for (let i = 0; i < vertexCount; i++) {
    const x = (graphCenterX + 300 * Math.cos(2 * Math.PI * i / vertexCount));
    const y = (graphCenterY + 300 * Math.sin(2 * Math.PI * i / vertexCount));
    coordX.push(x);
    coordY.push(y);
    drawVertex(x, y, i + 1);
  }
} else {
  const strongLength = strongLinked.length;
  for (let i = 0; i < strongLength; i++) {
    const x = (graphCenterX + 300 * Math.cos(2 * Math.PI * i / strongLength));
    const y = (graphCenterY + 300 * Math.sin(2 * Math.PI * i / strongLength));
    coordX.push(x);
    coordY.push(y);
    drawVertex(x, y, 'K' + (i + 1));
  }
}

const tweens = [];
let tween = false;

// Draw links on canvas
for (let i = 0; i < vertexCount; i++) {
  vertexList[i] = Vertex(i + 1);
  for (let j = 0; j < vertexCount; j++) {
    if (matrix[i][j]) {
      vertexList[i].increaseNegativeDegree();
      if (!isCondensed) {
        if (matrix[j][i] && !tweens.includes(`${i}${j}`)) {
          tween = true;
          tweens.push(`${j}${i}`);
        }
        lineDrawer(coordX[i], coordY[i], coordX[j], coordY[j], tween);
      }
    }
    if (matrix[j][i]) {
      vertexList[i].increasePositiveDegree();
    }
    tween = false;
  }
}

if (isCondensed) {
  condensedMatrix.forEach((row, i) => {
    row.forEach((el, j) => {
      if (condensedMatrix[i][j]) {
        if (matrix[j][i] && !tweens.includes(`${i}${j}`)) {
          tween = true;
          tweens.push(`${j}${i}`);
        }
        lineDrawer(coordX[i], coordY[i], coordX[j], coordY[j], tween);
        tween = false;
      }
    });
  });
}



// Show properities of each of vertex
console.table(vertexList,
  ['label', 'positiveDegree', 'negativeDegree', 'degree']);

console.log('Matrix of ways length of 2');
console.table(getMatrixOfWays(2));
console.log('Matrix of ways length of 3');
console.table(getMatrixOfWays(3));

console.log('Ways of 2');
getWays(2).forEach(row => console.log(...row));
console.log('Ways of 3');
getWays(3).forEach(row => console.log(...row));


console.log('Matrix of reachability');
console.table(getReachable());
console.log('Matrix of strong links');
console.table(getStrongLinkedMatrix());

console.log('Strong linked elements');
strongLinked
  .forEach((row, i) => console.log('K' + ++i + ': ' + row.map(el => ++el)));

console.log('Matrix of condnsed graph');
console.table(condensedMatrix);
