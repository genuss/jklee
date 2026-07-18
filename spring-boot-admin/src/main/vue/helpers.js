export function computeNextSessionName(appName, results) {
  const name = appName || '';
  const pattern = new RegExp('^' + escapeRegExp(name) + '_(\\d+)$');
  let max = 0;
  for (const result of results || []) {
    const match = pattern.exec(result.name);
    if (match) {
      const value = parseInt(match[1], 10);
      if (value > max) {
        max = value;
      }
    }
  }
  const next = String(max + 1).padStart(3, '0');
  return name + '_' + next;
}

export function formatEndedAt(endedAt) {
  if (!endedAt) return '';
  const date = new Date(endedAt);
  if (isNaN(date.getTime())) return endedAt;
  const pad = (n) => String(n).padStart(2, '0');
  const time = `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
  const now = new Date();
  const isToday =
    date.getFullYear() === now.getFullYear() &&
    date.getMonth() === now.getMonth() &&
    date.getDate() === now.getDate();
  if (isToday) return time;
  const day = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
  return `${day} ${time}`;
}

export function parseToMillis(timeString) {
  if (!timeString) return 0;

  const value = parseInt(timeString, 10);
  if (isNaN(value)) return 0;

  const unit = timeString.slice(-1);

  switch (unit) {
    case 's': return value * 1000;
    case 'm': return value * 60 * 1000;
    case 'h': return value * 60 * 60 * 1000;
    case 'd': return value * 60 * 60 * 24 * 1000;
    default: return value;
  }
}

function escapeRegExp(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}
