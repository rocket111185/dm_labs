'use strict';

const context = document.getElementById('canvas').getContext('2d');

const graphCenterX = 700;
const graphCenterY = 460;
const vertexCount = 11;
const radius = 30;

// The switch
const directed = true;

const coordX = [];
const coordY = [];
const vertexList = [];
const matrix = [
  [0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0],
  [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1],
  [1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0],
  [0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
];

const Vertex = label => {

    return {
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
    };
}

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

const lineDrawer = (x1, y1, x2, y2, pointed, tween) => {
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
    const midY = (y1 + y2) / 2 + 15;
    angle = getAngle(midX, midY, x2, y2);
    sin = angle[0];
    cos = angle[1];
    context.lineTo(midX, midY);
  }
  context.lineTo(x2, y2);
  context.stroke();
  if (pointed) {
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
  }
};


for (let i = 0; i < vertexCount; i++) {
  const x = (graphCenterX + 300 * Math.cos(2 * Math.PI * i / vertexCount));
  const y = (graphCenterY + 300 * Math.sin(2 * Math.PI * i / vertexCount));
  coordX.push(x);
  coordY.push(y);
  drawVertex(x, y, i + 1);
}

const tweens = [];
let tween = false;

for (let i = 0; i < vertexCount; i++) {
  vertexList[i] = Vertex(i + 1);
  for (let j = 0; j < vertexCount; j++) {
    if (matrix[i][j]) {
      vertexList[i].increaseNegativeDegree();
      if (matrix[j][i] && !tweens.includes(`${i}${j}`)) {
        tween = true;
        tweens.push(`${j}${i}`);
      }
      lineDrawer(coordX[i], coordY[i], coordX[j], coordY[j], directed, tween);
    }
    if (matrix[j][i]) {
      vertexList[i].increasePositiveDegree();
    }
    tween = false;
  }
}

console.table(vertexList,
  ['label', 'positiveDegree', 'negativeDegree', 'degree']);

console.log('List of isolated vertexes:');
vertexList.forEach(el => {
  if (el.degree === 0)
    console.log(el.label);
});

console.log('List of hanging vertexes:');
vertexList.forEach(el => {
  if (el.degree === 1)
    console.log(el.label);
});
