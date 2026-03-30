/**
 * İstatistik Hesaplama - Frontend Logic
 */

// ============================
// Real-time Input Filtering
// ============================
document.addEventListener('DOMContentLoaded', function () {
    const dataInput = document.getElementById('dataInput');

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

    // Integer-only number inputs: block non-numeric keys
    const integerInputs = [
        document.getElementById('populationSize'),
        document.getElementById('sampleSize'),
        document.getElementById('classCount'),
        document.getElementById('strataCount')
    ];

    integerInputs.forEach(input => {
        input.addEventListener('keydown', function (e) {
            const allowed = ['Backspace', 'Delete', 'Tab', 'Escape', 'Enter',
                'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'Home', 'End'];
            if (allowed.includes(e.key)) return;
            if ((e.ctrlKey || e.metaKey) && ['a', 'c', 'v', 'x'].includes(e.key.toLowerCase())) return;
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

    // Min/Max inputs: allow decimal numbers (digits, dot, minus)
    const decimalInputs = [
        document.getElementById('minValue'),
        document.getElementById('maxValue')
    ];

    decimalInputs.forEach(input => {
        input.addEventListener('input', function () {
            clearFieldError(this);
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

    // Input elements
    const minValueInput = document.getElementById('minValue');
    const maxValueInput = document.getElementById('maxValue');
    const populationSizeInput = document.getElementById('populationSize');
    const sampleSizeInput = document.getElementById('sampleSize');
    const dataInput = document.getElementById('dataInput');
    const dataSampleSizeInput = document.getElementById('dataSampleSize');

    // Reset
    errorDiv.style.display = 'none';
    resultsDiv.style.display = 'none';
    [minValueInput, maxValueInput, populationSizeInput, sampleSizeInput, dataInput, dataSampleSizeInput].forEach(clearFieldError);

    // Read values
    const minVal = minValueInput.value.trim();
    const maxVal = maxValueInput.value.trim();
    const popSizeVal = populationSizeInput.value.trim();
    const sampleSizeVal = sampleSizeInput.value.trim();
    const rawData = dataInput.value.trim();
    const dataSampleSizeVal = dataSampleSizeInput ? dataSampleSizeInput.value.trim() : '';
    const classCountVal = document.getElementById('classCount').value.trim();
    const strataCountVal = document.getElementById('strataCount').value.trim();

    // Determine which sections have data
    const hasSamplingParams = minVal || maxVal || popSizeVal || sampleSizeVal;
    const hasDataset = !!rawData;

    // Must fill at least one section
    if (!hasSamplingParams && !hasDataset) {
        showError('Lütfen örneklem parametrelerini veya veri setini giriniz.');
        markFieldError(minValueInput);
        minValueInput.focus();
        return;
    }

    // Build request
    const request = {};

    // Validate sampling parameters if provided
    if (hasSamplingParams) {
        // All 4 fields required
        if (!minVal) {
            showError('Lütfen min değer giriniz.');
            markFieldError(minValueInput);
            minValueInput.focus();
            return;
        }
        if (!maxVal) {
            showError('Lütfen max değer giriniz.');
            markFieldError(maxValueInput);
            maxValueInput.focus();
            return;
        }
        if (!popSizeVal) {
            showError('Lütfen Büyük N (popülasyon büyüklüğü) giriniz.');
            markFieldError(populationSizeInput);
            populationSizeInput.focus();
            return;
        }
        if (!sampleSizeVal) {
            showError('Lütfen Küçük n (örneklem büyüklüğü) giriniz.');
            markFieldError(sampleSizeInput);
            sampleSizeInput.focus();
            return;
        }

        const minValue = parseFloat(minVal);
        const maxValue = parseFloat(maxVal);
        const populationSize = parseInt(popSizeVal);
        const sampleSize = parseInt(sampleSizeVal);

        if (isNaN(minValue)) {
            showError('Min değer geçerli bir sayı olmalıdır.');
            markFieldError(minValueInput);
            minValueInput.focus();
            return;
        }
        if (isNaN(maxValue)) {
            showError('Max değer geçerli bir sayı olmalıdır.');
            markFieldError(maxValueInput);
            maxValueInput.focus();
            return;
        }
        if (minValue >= maxValue) {
            showError('Min değer, max değerden küçük olmalıdır.');
            markFieldError(minValueInput);
            markFieldError(maxValueInput);
            minValueInput.focus();
            return;
        }
        if (isNaN(populationSize) || populationSize <= 0) {
            showError('Büyük N pozitif bir tam sayı olmalıdır.');
            markFieldError(populationSizeInput);
            populationSizeInput.focus();
            return;
        }
        if (isNaN(sampleSize) || sampleSize <= 0) {
            showError('Küçük n pozitif bir tam sayı olmalıdır.');
            markFieldError(sampleSizeInput);
            sampleSizeInput.focus();
            return;
        }
        if (sampleSize > populationSize) {
            showError(`Küçük n (${sampleSize}), Büyük N'den (${populationSize}) büyük olamaz.`);
            markFieldError(sampleSizeInput);
            sampleSizeInput.focus();
            return;
        }

        request.minValue = minValue;
        request.maxValue = maxValue;
        request.populationSize = populationSize;
        request.sampleSize = sampleSize;
        request.strataCount = strataCountVal ? parseInt(strataCountVal) : null;
    } else {
        // sampleSize still needed for backend (set to 0, won't be used for sampling)
        request.sampleSize = 0;
    }

    // Validate dataset if provided
    if (hasDataset) {
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

        request.data = data;
        request.classCount = classCountVal ? parseInt(classCountVal) : null;
        
        if (dataSampleSizeVal) {
            const dataSampleSize = parseInt(dataSampleSizeVal);
            if (isNaN(dataSampleSize) || dataSampleSize <= 0) {
                showError('Veri seti örneklem büyüklüğü pozitif bir tam sayı olmalıdır.');
                markFieldError(dataSampleSizeInput);
                dataSampleSizeInput.focus();
                return;
            }
            if (dataSampleSize > data.length) {
                showError(`Örneklem büyüklüğü (${dataSampleSize}), veri sayısından (${data.length}) büyük olamaz.`);
                markFieldError(dataSampleSizeInput);
                dataSampleSizeInput.focus();
                return;
            }
            request.dataSampleSize = dataSampleSize;
        } else {
            request.dataSampleSize = null;
        }
    }

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

        renderResults(result, request);
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

function renderResults(data, request) {
    const resultsDiv = document.getElementById('results');

    // Show/hide sampling sections based on availability
    const hasSampling = data.uretilenPopulasyon && data.uretilenPopulasyon.length > 0;
    const hasDataset = data.basitSeri && data.basitSeri.length > 0;

    // Üretilen Popülasyon
    const popSection = document.getElementById('section-pop');
    if (hasSampling) {
        popSection.style.display = '';
        document.getElementById('pop-count').textContent = data.uretilenPopulasyon.length;
        renderSimpleTable('table-pop', data.uretilenPopulasyon);
    } else {
        popSection.style.display = 'none';
    }

    // Basit Rastgele Örneklem
    const broSection = document.getElementById('section-bro');
    if (hasSampling && data.basitRastgeleOrneklem) {
        broSection.style.display = '';
        document.getElementById('bro-count').textContent = data.basitRastgeleOrneklem.length;
        renderSimpleTable('table-bro', data.basitRastgeleOrneklem);
    } else {
        broSection.style.display = 'none';
    }

    // Sistematik Örneklem
    const soSection = document.getElementById('section-so');
    if (hasSampling && data.sistematikOrneklem) {
        soSection.style.display = '';
        const k = Math.floor(request.populationSize / request.sampleSize);
        document.getElementById('so-k').textContent = k;
        renderSimpleTable('table-so', data.sistematikOrneklem);
    } else {
        soSection.style.display = 'none';
    }

    // Tabakalı Örneklem
    const toSection = document.getElementById('section-to');
    if (hasSampling && data.tabakaliOrneklem) {
        toSection.style.display = '';
        renderStratifiedTable('table-to', data.tabakaliOrneklem);
    } else {
        toSection.style.display = 'none';
    }

    // Veri Seti Örneklemi
    const vsoSection = document.getElementById('section-vso');
    if (hasDataset && data.veriSetiOrneklem) {
        vsoSection.style.display = '';
        document.getElementById('vso-count').textContent = data.veriSetiOrneklem.length;
        renderSimpleTable('table-vso', data.veriSetiOrneklem);
    } else {
        vsoSection.style.display = 'none';
    }

    // Basit Seri
    const bsSection = document.getElementById('section-bs');
    if (hasDataset) {
        bsSection.style.display = '';
        renderSimpleTable('table-bs', data.basitSeri);
    } else {
        bsSection.style.display = 'none';
    }

    // Frekans Serisi
    const fsSection = document.getElementById('section-fs');
    if (hasDataset && data.frekansSeries) {
        fsSection.style.display = '';
        renderFrequencySeries('table-fs', data.frekansSeries);
    } else {
        fsSection.style.display = 'none';
    }

    // Frekans Tablosu
    const ftSection = document.getElementById('section-ft');
    if (hasDataset && data.frekansTablosu) {
        ftSection.style.display = '';
        renderFrequencyTable('table-ft', data.frekansTablosu);
    } else {
        ftSection.style.display = 'none';
    }

    // Show with animation
    resultsDiv.style.display = 'block';

    // Scroll to first visible result
    setTimeout(() => {
        const firstVisible = hasSampling ? 'section-pop' : 'section-bs';
        document.getElementById(firstVisible).scrollIntoView({
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
