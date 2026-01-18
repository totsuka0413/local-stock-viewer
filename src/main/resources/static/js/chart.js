async function renderCloseChart(symbol, range) {
  const canvas = document.getElementById("priceChart");
  const noData = document.getElementById("noData");

  const url = `/api/stocks/${encodeURIComponent(symbol)}/prices?range=${encodeURIComponent(range)}`;
  const res = await fetch(url);
  const data = await res.json();

  if (!data || !data.labels || data.labels.length === 0) {
    if (noData) noData.style.display = "block";
    return;
  }
  if (noData) noData.style.display = "none";

  const styles = getComputedStyle(document.documentElement);
  const grid = styles.getPropertyValue("--grid").trim();
  const text = styles.getPropertyValue("--muted").trim();
  const main = styles.getPropertyValue("--text").trim();

  const ctx = canvas.getContext("2d");

  if (window.__chart) window.__chart.destroy();

  window.__chart = new Chart(ctx, {
    type: "line",
    data: {
      labels: data.labels,
      datasets: [{
        label: "Close",
        data: data.closes,
        tension: 0.25,
        pointRadius: 0,
        borderWidth: 2
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { labels: { color: main } },
        tooltip: { intersect: false, mode: "index" }
      },
      scales: {
        x: {
          grid: { color: grid },
          ticks: { color: text, maxTicksLimit: 8 }
        },
        y: {
          grid: { color: grid },
          ticks: { color: text }
        }
      }
    }
  });
}
