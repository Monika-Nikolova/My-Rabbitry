(function() {
  const container = document.getElementById('leaves-container');
  if (!container) return;

  const leafColors = ['#6fbf73', '#86c97d', '#5cab65', '#7cc286', '#f573e8', '#f598eb', '#e3a6dc'];
  const leafCount = Math.min(30, Math.max(12, Math.floor(window.innerWidth / 40)));

  for (let i = 0; i < leafCount; i++) {
    const leaf = document.createElement('div');
    leaf.className = 'leaf';
    const left = Math.random() * 100;
    const delay = Math.random() * 8;
    const duration = 6 + Math.random() * 10;
    const size = 12 + Math.random() * 16;
    leaf.style.left = left + 'vw';
    leaf.style.animationDuration = duration + 's, ' + (3 + Math.random() * 3) + 's';
    leaf.style.animationDelay = delay + 's, ' + (delay/2) + 's';
    leaf.style.width = size + 'px';
    leaf.style.height = size + 'px';
    leaf.style.background = leafColors[Math.floor(Math.random()*leafColors.length)];
    leaf.style.opacity = (0.5 + Math.random()*0.5).toString();
    container.appendChild(leaf);
  }
})();


