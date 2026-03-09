/**
 * İstatistik Hesaplama - Frontend Logic
 */

// ============================
// Real-time Input Filtering
// ============================
document.addEventListener('DOMContentLoaded', function () {
    const dataInput = document.getElementById('dataInput');
    const sampleSizeInput = document.getElementById('sampleSize');
    const classCountInput = document.getElementById('classCount');

    // Data textarea: only allow digits, commas, dots, spaces, semicolons, minus, newlines
    dataInput.addEventListener('input', function () {
        const cursorPos = this.selectionStart;
        const before = this.value;
        this.value = this.value.replace(/[^0-9.,;\s\-]/g, '');
        if (this.value !== before) {
            this.selectionStart = this.selectionEnd = Math.max(0, cursorPos - 1);
            shakeField(this);
        }
        clearFieldError(this);
    });

    dataInput.addEventListener('paste', function (e) {
        e.preventDefault();
        const text = (e.clipboardData || window.clipboardData).getData('text');
        const cleaned = text.replace(/[^0-9.,;\s\-]/g, '');
        document.execCommand('insertText', false, cleaned);
    });

    // Number inputs: block non-numeric keys
    [sampleSizeInput, classCountInput, document.getElementById('strataCount')].forEach(input => {
        input.addEventListener('keydown', function (e) {
            // Allow: backspace, delete, tab, escape, enter, arrows
            const allowed = ['Backspace', 'Delete', 'Tab', 'Escape', 'Enter',
                'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'Home', 'End'];
            if (allowed.includes(e.key)) return;
            // Allow Ctrl+A, Ctrl+C, Ctrl+V, Ctrl+X
            if ((e.ctrlKey || e.metaKey) && ['a', 'c', 'v', 'x'].includes(e.key.toLowerCase())) return;
            // Block anything that isn't a digit
            if (!/^[0-9]$/.test(e.key)) {
                e.preventDefault();
                shakeField(this);
            }
        });

        input.addEventListener('input', function () {
            this.value = this.value.replace(/[^0-9]/g, '');
            clearFieldError(this);
        });

        input.addEventListener('paste', function (e) {
            e.preventDefault();
            const text = (e.clipboardData || window.clipboardData).getData('text');
            const cleaned = text.replace(/[^0-9]/g, '');
            document.execCommand('insertText', false, cleaned);
        });
    });
});

// ============================
// Field Error Helpers
// ============================
function markFieldError(el) {
    el.classList.add('field-error');
    shakeField(el);
}

function clearFieldError(el) {
    el.classList.remove('field-error');
}

function shakeField(el) {
    el.classList.remove('shake');
    // Force reflow to restart animation
    void el.offsetWidth;
    el.classList.add('shake');
    el.addEventListener('animationend', () => el.classList.remove('shake'), { once: true });
}

// ============================
// Main Calculation
// ============================
async function calculate() {
    const btn = document.getElementById('calculateBtn');
    const errorDiv = document.getElementById('errorMessage');
    const resultsDiv = document.getElementById('results');

    const dataInput = document.getElementById('dataInput');
    const sampleSizeInput = document.getElementById('sampleSize');

    // Reset
    errorDiv.style.display = 'none';
    resultsDiv.style.display = 'none';
    clearFieldError(dataInput);
    clearFieldError(sampleSizeInput);

    // Parse input
    const rawData = dataInput.value.trim();
    const sampleSizeVal = sampleSizeInput.value.trim();
    const classCountVal = document.getElementById('classCount').value.trim();

    // Validate: empty data
    if (!rawData) {
        showError('Lütfen veri seti giriniz.');
        markFieldError(dataInput);
        dataInput.focus();
        return;
    }

    // Validate: empty sample size
    if (!sampleSizeVal) {
        showError('Lütfen örneklem büyüklüğü giriniz.');
        markFieldError(sampleSizeInput);
        sampleSizeInput.focus();
        return;
    }

    // Parse data: support comma, semicolon, space, tab separators
    const data = rawData.split(/[,;\s]+/)
        .map(s => s.trim())
        .filter(s => s.length > 0)
        .map(s => parseFloat(s));

    if (data.length === 0 || data.some(isNaN)) {
        showError('Veri setinde geçersiz değer(ler) var. Lütfen sadece sayı giriniz.');
        markFieldError(dataInput);
        dataInput.focus();
        return;
    }

    const sampleSize = parseInt(sampleSizeVal);
    if (isNaN(sampleSize) || sampleSize <= 0) {
        showError('Örneklem büyüklüğü pozitif bir tam sayı olmalıdır.');
        markFieldError(sampleSizeInput);
        sampleSizeInput.focus();
        return;
    }

    if (sampleSize > data.length) {
        showError(`Örneklem büyüklüğü (${sampleSize}), veri sayısından (${data.length}) büyük olamaz.`);
        markFieldError(sampleSizeInput);
        sampleSizeInput.focus();
        return;
    }

    const strataCountVal = document.getElementById('strataCount').value.trim();

    const request = {
        data: data,
        sampleSize: sampleSize,
        classCount: classCountVal ? parseInt(classCountVal) : null,
        strataCount: strataCountVal ? parseInt(strataCountVal) : null
    };

    // Loading state
    btn.classList.add('loading');
    btn.querySelector('.btn-text').textContent = 'Hesaplanıyor...';

    try {
        const response = await fetch('/api/calculate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(request)
        });

        const result = await response.json();

        if (!response.ok) {
            showError(result.error || 'Bir hata oluştu.');
            return;
        }

        renderResults(result, data.length, sampleSize);
    } catch (err) {
        showError('Sunucuya bağlanılamadı. Lütfen uygulamanın çalıştığından emin olun.');
    } finally {
        btn.classList.remove('loading');
        btn.querySelector('.btn-text').textContent = 'Hesapla';
    }
}

function showError(message) {
    const errorDiv = document.getElementById('errorMessage');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
}

function renderResults(data, totalN, sampleSize) {
    const resultsDiv = document.getElementById('results');

    // Basit Rastgele Örneklem
    document.getElementById('bro-count').textContent = sampleSize;
    renderSimpleTable('table-bro', data.basitRastgeleOrneklem);

    // Sistematik Örneklem
    const k = Math.floor(totalN / sampleSize);
    document.getElementById('so-k').textContent = k;
    renderSimpleTable('table-so', data.sistematikOrneklem);

    // Tabakalı Örneklem
    renderStratifiedTable('table-to', data.tabakaliOrneklem);

    // Basit Seri
    renderSimpleTable('table-bs', data.basitSeri);

    // Frekans Serisi
    renderFrequencySeries('table-fs', data.frekansSeries);

    // Frekans Tablosu
    renderFrequencyTable('table-ft', data.frekansTablosu);

    // Show with animation
    resultsDiv.style.display = 'block';

    // Scroll to results smoothly
    setTimeout(() => {
        document.getElementById('section-bro').scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }, 100);
}

function renderSimpleTable(tableId, values) {
    const tbody = document.querySelector(`#${tableId} tbody`);
    tbody.innerHTML = '';

    values.forEach((val, index) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${index + 1}</td><td>${formatValue(val)}</td>`;
        tbody.appendChild(tr);
    });
}

function renderFrequencySeries(tableId, freqMap) {
    const tbody = document.querySelector(`#${tableId} tbody`);
    tbody.innerHTML = '';

    // freqMap is an object { value: count }
    const entries = Object.entries(freqMap)
        .map(([k, v]) => [parseFloat(k), v])
        .sort((a, b) => a[0] - b[0]);

    entries.forEach(([value, count]) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${formatValue(value)}</td><td>${count}</td>`;
        tbody.appendChild(tr);
    });
}

function renderFrequencyTable(tableId, rows) {
    const tbody = document.querySelector(`#${tableId} tbody`);
    tbody.innerHTML = '';

    rows.forEach(row => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${row.classInterval}</td>
            <td>${row.frequency}</td>
            <td>${(row.relativeFrequency * 100).toFixed(2)}%</td>
            <td>${row.cumulativeFrequency}</td>
            <td>${(row.cumulativeRelativeFrequency * 100).toFixed(2)}%</td>
        `;
        tbody.appendChild(tr);
    });
}

function formatValue(val) {
    if (Number.isInteger(val)) {
        return val.toString();
    }
    return parseFloat(val.toFixed(4)).toString();
}

function renderStratifiedTable(tableId, rows) {
    const tbody = document.querySelector(`#${tableId} tbody`);
    tbody.innerHTML = '';

    rows.forEach(row => {
        const tr = document.createElement('tr');
        const values = row.secilenDegerler.map(v => formatValue(v)).join(', ');
        tr.innerHTML = `
            <td>${row.tabakaAdi}</td>
            <td>${row.tabakaBuyuklugu}</td>
            <td>${row.secilenAdet}</td>
            <td>${values}</td>
        `;
        tbody.appendChild(tr);
    });
}

// Allow Enter key to trigger calculation
document.addEventListener('keydown', function (e) {
    if (e.key === 'Enter' && e.target.tagName !== 'TEXTAREA') {
        calculate();
    }
});
