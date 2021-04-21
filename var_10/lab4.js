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

const matrix = [
  [1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0],
  [1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0],
  [1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0],
  [0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1],
  [1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0],
  [0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0],
  [0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1],
  [0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0]
];

const vertexCount = matrix.length;
const tree = Array(vertexCount * vertexCount).fill(0);
const path = [];
let unopened = Array(vertexCount)
  .fill(0)
  .map((el, i) => i);
const first = prompt('Номер першої вершини?') - 1;
if (first === null || first < 0 || first > vertexCount - 1) {
  alert('Введено некоректне значення');
  throw new Error('Invalid argument');
}

const drawVertex = (x, y, text) => {
  context.beginPath();
  context.arc(x, y, radius, 0, 2 * Math.PI);
  context.stroke();
  context.font = '24px serif';
  context.fillText(text, x - 6, y + 8);
};

const drawActive = vertex => {
  const x = coordX[vertex];
  const y = coordY[vertex];
  const text = ++vertex;
  context.beginPath();
  context.arc(x, y, radius, 0, 2 * Math.PI);
  context.fillStyle = '#f90000';
  context.fill();
  context.stroke();
  context.fillStyle = 'black';
  context.font = '24px serif';
  context.fillText(text, x - 6, y + 8);
};

const drawVisiting = vertex => {
  const x = coordX[vertex];
  const y = coordY[vertex];
  const text = ++vertex;
  context.beginPath();
  context.arc(x, y, radius, 0, 2 * Math.PI);
  context.fillStyle = '#019efb';
  context.fill();
  context.stroke();
  context.fillStyle = 'black';
  context.font = '24px serif';
  context.fillText(text, x - 6, y + 8);
};

const drawVisited = vertex => {
  const x = coordX[vertex];
  const y = coordY[vertex];
  const text = ++vertex;
  context.beginPath();
  context.arc(x, y, radius, 0, 2 * Math.PI);
  context.fillStyle = '#016793';
  context.fill();
  context.stroke();
  context.fillStyle = 'black';
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

const lineDrawer = (x1, y1, x2, y2, tween, colored) => {
  context.beginPath();
  if (colored) context.strokeStyle = '#029efb';
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
    const midY = (y1 + y2) / 2 + 15;
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
  context.strokeStyle = 'black';
};

const getSpine = () => {
  let temp = [];
  let firstStep = 0;
  while (true) {
    const stepTemp = [];
    if (!firstStep) {
      for (let i = 1; i < vertexCount; i++) {
        if (matrix[first][i]) {
          stepTemp.push(first, i);
          break;
        }
      }
      firstStep++;
    } else {
      const row = temp;
      const i = row[row.length - 1];
      const possible = Array(vertexCount)
        .fill(0)
        .map((el, i) => i)
        .filter(el => !row.includes(el));
      for (let j = 0; j < vertexCount; j++) {
        if (matrix[i][j] && i !== j && possible.includes(j)) {
          stepTemp.push(...row, j);
          break;
        }
      }
    }
    if (!stepTemp.length) break;
    temp = stepTemp;
  }
  if (!temp.length) {
    alert('З вершини немає виходу');
    throw new Error('The way cannot be construsted');
  }
  return temp;
};

const spine = getSpine();
path.push(...spine);
unopened = unopened.filter(el => !spine.includes(el));
const backSpine = getSpine().reverse();
backSpine.shift();
let active = spine.shift();
let nextVertex;
let nextActive;
let task = 0;
let goForward = true;
const toVisit = [];

const treeHelper = (a, b) => {
  lineDrawer(coordX[a], coordY[a],
    coordX[b], coordY[b],
    false, true);
  lineDrawer(treeX[a], treeY[a],
    treeX[b], treeY[b],
    false, false);
  tree[a * vertexCount + b] = 1;
};

const dfsHelper = () => {
  if (goForward) {
    nextActive = spine.shift();
    treeHelper(active, nextActive);
  } else {
    nextActive = backSpine.shift();
    unopened.forEach(el => {
      if (matrix[nextActive][el]) {
        toVisit.push(el);
        path.push(el);
      }
    });
    unopened = unopened.filter(el => !toVisit.includes(el));
  }
};

const depthBinarySearch = () => {
  if (!task) {
    if (nextActive !== undefined) {
      drawVisited(active);
      active = nextActive;
    }
    drawActive(active);
    if (!spine.length && goForward) {
      goForward = false;
    }
    if (!backSpine.length) {
      task = 4;
      return;
    }
    if (toVisit.length) task = 2;
    else task = 1;
  } else if (task === 1) {
    dfsHelper();
    drawVisiting(nextActive);
    tree[active * vertexCount + nextVertex] = 1;
    task = 0;
  } else if (task === 2) {
    nextVertex = toVisit.shift();
    drawVisiting(nextVertex);
    treeHelper(active, nextVertex);
    task = 3;
  } else if (task === 3) {
    drawVisited(nextVertex);
    if (!toVisit.length)
      task = 0;
    else
      task = 2;
  } else if (task === 4) {
    drawVisited(active);
    for (let i = 0; i < vertexCount; i++) {
      console.log(tree
        .slice(i * vertexCount, (i + 1) * vertexCount)
        .join('\t'));
    }
    console.log(path
      .map(el => ++el)
      .join(', '));
    button.removeEventListener('click', depthBinarySearch);
    if (unopened.length)
      alert('Відвідані не всі вершини. Спробуйте обрати іншу вершину.');
  }
};

button.addEventListener('click', depthBinarySearch);

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
      lineDrawer(coordX[i], coordY[i], coordX[j], coordY[j], tween, false);
    }
    tween = false;
  }
}
