/**
 * İstatistik Hesaplama - Frontend Logic (Módüler Sistem)
 */

// Helper: Hata göster
function showError(errorId, msg, inputElements=[]) {
    const errorDiv = document.getElementById(errorId);
    if(errorDiv) {
        errorDiv.textContent = msg;
        errorDiv.style.display = 'block';
    }
    inputElements.forEach(el => { if(el) markFieldError(el); });
}

function hideError(errorId, inputElements=[]) {
    const errorDiv = document.getElementById(errorId);
    if(errorDiv) errorDiv.style.display = 'none';
    inputElements.forEach(el => { if(el) clearFieldError(el); });
}

function markFieldError(el) { el.classList.add('field-error'); shakeField(el); }
function clearFieldError(el) { el.classList.remove('field-error'); }
function shakeField(el) {
    el.classList.remove('shake');
    void el.offsetWidth;
    el.classList.add('shake');
    el.addEventListener('animationend', () => el.classList.remove('shake'), { once: true });
}

function gVal(id) {
    const el = document.getElementById(id);
    return el ? el.value.trim() : '';
}

// ----------------------------------------------------
// API REQUEST HELPER
// ----------------------------------------------------
async function sendCalcRequest(requestBody, btnEl) {
    const origText = btnEl.textContent;
    btnEl.classList.add('loading');
    btnEl.textContent = 'Hesaplanıyor...';

    try {
        const response = await fetch('/api/calculate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });

        const result = await response.json();
        if (!response.ok) {
            throw new Error(result.error || 'Bilinmeyen API hatası');
        }
        return result;
    } finally {
        btnEl.classList.remove('loading');
        btnEl.textContent = origText;
    }
}

// ----------------------------------------------------
// MODULE 1: Basit Rastgele Örneklem
// ----------------------------------------------------
async function calcBasitRastgele(btn) {
    const minEl = document.getElementById('brMin');
    const maxEl = document.getElementById('brMax');
    const nEl = document.getElementById('brN');
    const resultDiv = document.getElementById('brResult');
    
    hideError('brError', [minEl, maxEl, nEl]);
    resultDiv.style.display = 'none';

    if(!gVal('brMin') || !gVal('brMax') || !gVal('brN')) {
        return showError('brError', 'Min, Max ve n değerlerini doldurunuz.', [minEl, maxEl, nEl]);
    }

    const request = {
        basRastMin: parseInt(gVal('brMin')),
        basRastMax: parseInt(gVal('brMax')),
        basRastN: parseInt(gVal('brN')) 
    };

    if (isNaN(request.basRastMin) || isNaN(request.basRastMax) || isNaN(request.basRastN)){
         return showError('brError', 'Geçerli tam sayılar giriniz.', [minEl, maxEl, nEl]);
    }
    if (request.basRastMin >= request.basRastMax) {
        return showError('brError', 'Min değer Max değerden küçük olmalıdır.', [minEl, maxEl]);
    }

    try {
        const data = await sendCalcRequest(request, btn);
        if(data.basitRastgeleOrneklem) {
            document.getElementById('brCount').textContent = data.basitRastgeleOrneklem.length;
            renderSimpleTable('brTable', data.basitRastgeleOrneklem);
            resultDiv.style.display = 'block';
        }
    } catch (e) {
        showError('brError', e.message);
    }
}

// ----------------------------------------------------
// MODULE 2: Sistematik Örneklem
// ----------------------------------------------------
async function calcSistematik(btn) {
    const elList = [document.getElementById('sysBuyukN'), document.getElementById('sysKucukN')];
    const resultDiv = document.getElementById('sysResult');

    hideError('sysError', elList);
    resultDiv.style.display = 'none';

    if(elList.some(e => !e.value.trim())) {
        return showError('sysError', 'Lütfen tüm alanları doldurunuz.', elList.filter(e => !e.value.trim()));
    }

    const request = {
        sisBuyukN: parseInt(gVal('sysBuyukN')),
        sisKucukN: parseInt(gVal('sysKucukN'))
    };

    if(request.sisBuyukN <= 0 || request.sisKucukN <= 0 || request.sisKucukN > request.sisBuyukN) {
        return showError('sysError', 'Geçersiz popülasyon/örneklem büyüklüğü.', [elList[0], elList[1]]);
    }

    try {
        const data = await sendCalcRequest(request, btn);
        if(data.sistematikOrneklem) {
            document.getElementById('sysK').textContent = Math.floor(request.sisBuyukN / request.sisKucukN);
            renderSimpleTable('sysTable', data.sistematikOrneklem);
            resultDiv.style.display = 'block';
        }
    } catch(e) {
        showError('sysError', e.message);
    }
}

// ----------------------------------------------------
// MODULE 3: Tabakalı Örneklem
// ----------------------------------------------------

function generateStrataInputs() {
    const container = document.getElementById('strataRatiosContainer');
    const grid = document.getElementById('strataInputsGrid');
    const val = parseInt(document.getElementById('tabSayisi').value);
    
    // Clear and hide if invalid
    if (isNaN(val) || val < 1) {
        grid.innerHTML = '';
        container.style.display = 'none';
        return;
    }
    
    // Cap to 100 visually so browser doesn't hang, max is already set to 30 in HTML
    const count = Math.min(val, 100);
    
    let html = '';
    for(let i=1; i<=count; i++) {
        html += `
        <div class="form-group" style="margin: 0; min-width: 90px;">
            <label for="tabO_${i}" style="font-size: 0.85rem;">${i}. Tabaka Oranı</label>
            <input type="number" id="tabO_${i}" class="dyn-tab-oran" placeholder="Örn: 50" min="0" step="any" oninput="clearFieldError(this)" style="padding: 0.6rem;">
        </div>`;
    }
    
    grid.innerHTML = html;
    container.style.display = 'block';
}

async function calcTabakali(btn) {
    const tabSayisiEl = document.getElementById('tabSayisi');
    const elList = [document.getElementById('tabMin'), document.getElementById('tabMax'), 
                    document.getElementById('tabBuyukN'), document.getElementById('tabKucukN'),
                    tabSayisiEl];
    const resultDiv = document.getElementById('tabResult');

    hideError('tabError', elList);
    resultDiv.style.display = 'none';

    if(elList.some(e => !e || !e.value.trim())) {
        return showError('tabError', 'Lütfen Tabaka Sayısı dahil tüm alanları doldurunuz.', elList.filter(e => !e || !e.value.trim()));
    }
    
    const count = parseInt(tabSayisiEl.value);
    let ratioList = [];
    let ratioEls = [];
    for(let i = 1; i <= count; i++) {
        const oEl = document.getElementById('tabO_' + i);
        if(oEl) {
            ratioEls.push(oEl);
            if(!oEl.value.trim()) {
                return showError('tabError', 'Tüm tabaka oranları doldurulmalıdır.', ratioEls);
            }
            ratioList.push(oEl.value.trim());
        }
    }
    
    if(ratioList.length === 0) {
        return showError('tabError', 'En az 1 tabaka ve oranı girilmelidir.', [tabSayisiEl]);
    }

    const request = {
        tabMin: parseFloat(gVal('tabMin')),
        tabMax: parseFloat(gVal('tabMax')),
        tabBuyukN: parseInt(gVal('tabBuyukN')),
        tabKucukN: parseInt(gVal('tabKucukN')),
        tabOranlar: ratioList.join(',')
    };

    if(request.tabMin >= request.tabMax) return showError('tabError', 'Min < Max olmalı.');
    if(request.tabBuyukN <= 0 || request.tabKucukN <= 0 || request.tabKucukN > request.tabBuyukN) {
        return showError('tabError', 'Geçersiz popülasyon boyutları büyük N > küçük N olmalı.');
    }

    try {
        const data = await sendCalcRequest(request, btn);
        if(data.tabakaliOrneklem) {
            renderTabakaliTable('tabTable', data.tabakaliOrneklem);
            resultDiv.style.display = 'block';
        }
    } catch(e) {
        showError('tabError', e.message);
    }
}

// ----------------------------------------------------
// MODULE 4: Dataset
// ----------------------------------------------------
async function calcData(type, btn) {
    const dsInput = document.getElementById('dsInput');
    const resultDiv = document.getElementById('dsResult');
    const container = document.getElementById('dsTableContainer');
    hideError('dsError', [dsInput]);
    resultDiv.style.display = 'none';
    container.innerHTML = '';

    const rawData = gVal('dsInput');
    if(!rawData) {
        return showError('dsError', 'Lütfen veri seti giriniz.', [dsInput]);
    }

    const numbers = rawData.split(/[,;\s]+/).filter(s => s.trim().length > 0).map(s => parseFloat(s));
    if (numbers.length === 0 || numbers.some(isNaN)) {
        return showError('dsError', 'Veri setinde geçersiz sayılar var.', [dsInput]);
    }

    const request = { data: numbers };

    if(type === 'frekansTablosu') {
        const cl = gVal('dsClassCount');
        if(cl) request.classCount = parseInt(cl);
    } else if(type === 'veriOrneklemi') {
        const dsN = gVal('dsSampleN');
        if(!dsN) return showError('dsError', 'Örneklem çekmek için, "Örneklem (n)" alanını doldurun.', [document.getElementById('dsSampleN')]);
        
        const nn = parseInt(dsN);
        if(isNaN(nn) || nn <= 0 || nn > numbers.length) {
            return showError('dsError', 'Geçersiz örneklem büyüklüğü (Veri setinden büyük olamaz).');
        }
        request.dataSampleSize = nn;
    }

    try {
        const responseData = await sendCalcRequest(request, btn);

        const buildTable = (html) => `<table style="width:100%; text-align:left; border-collapse:collapse;">${html}</table>`;
        const fmt = (v) => Number.isInteger(v) ? v.toString() : parseFloat(v.toFixed(4)).toString();

        let htmlOut = "";

        if(type === 'basitSeri' && responseData.basitSeri) {
            document.getElementById('dsTitle').textContent = `Basit Seri (${responseData.basitSeri.length} eleman)`;
            let rows = responseData.basitSeri.map((v, i) => `<tr><td style="padding:8px; border-bottom:1px solid #444">${i+1}</td><td style="padding:8px; border-bottom:1px solid #444">${v}</td></tr>`).join('');
            htmlOut = buildTable(`<thead><tr><th style="padding:8px; border-bottom:1px solid #555">Sıra</th><th style="padding:8px; border-bottom:1px solid #555">Değer</th></tr></thead><tbody>${rows}</tbody>`);
        } 
        else if (type === 'frekansSerisi' && responseData.frekansSeries) {
            document.getElementById('dsTitle').textContent = `Frekans Serisi`;
            const entries = Object.entries(responseData.frekansSeries).map(([k, v]) => [parseFloat(k), v]).sort((a,b) => a[0]-b[0]);
            let rows = entries.map(([v, count]) => `<tr><td style="padding:8px; border-bottom:1px solid #444">${v}</td><td style="padding:8px; border-bottom:1px solid #444">${count}</td></tr>`).join('');
            htmlOut = buildTable(`<thead><tr><th style="padding:8px; border-bottom:1px solid #555">Değer (xᵢ)</th><th style="padding:8px; border-bottom:1px solid #555">Frekans (fᵢ)</th></tr></thead><tbody>${rows}</tbody>`);
        }
        else if (type === 'frekansTablosu' && responseData.frekansTablosu) {
            document.getElementById('dsTitle').textContent = `Frekans Tablosu (${responseData.frekansTablosu.length} sınıf)`;
            let rows = responseData.frekansTablosu.map(r => `
                <tr>
                    <td style="padding:8px; border-bottom:1px solid #444">${r.classInterval}</td>
                    <td style="padding:8px; border-bottom:1px solid #444">${fmt(r.altSinir)}</td>
                    <td style="padding:8px; border-bottom:1px solid #444">${fmt(r.ustSinir)}</td>
                    <td style="padding:8px; border-bottom:1px solid #444">${fmt(r.ortaNokta)}</td>
                    <td style="padding:8px; border-bottom:1px solid #444">${r.frequency}</td>
                    <td style="padding:8px; border-bottom:1px solid #444">${(r.relativeFrequency * 100).toFixed(2)}%</td>
                    <td style="padding:8px; border-bottom:1px solid #444">${r.cumulativeFrequency}</td>
                    <td style="padding:8px; border-bottom:1px solid #444">${(r.cumulativeRelativeFrequency * 100).toFixed(2)}%</td>
                </tr>
            `).join('');
            htmlOut = buildTable(`<thead><tr>
                <th style="padding:8px; border-bottom:1px solid #555">Sınıf Limitleri</th>
                <th style="padding:8px; border-bottom:1px solid #555">Alt Sınır</th>
                <th style="padding:8px; border-bottom:1px solid #555">Üst Sınır</th>
                <th style="padding:8px; border-bottom:1px solid #555">Orta Nokta (yₖ)</th>
                <th style="padding:8px; border-bottom:1px solid #555">Frekans (fᵢ)</th>
                <th style="padding:8px; border-bottom:1px solid #555">Bağıl (%)</th>
                <th style="padding:8px; border-bottom:1px solid #555">Eklemeli Frekans</th>
                <th style="padding:8px; border-bottom:1px solid #555">Küm. Bağıl (%)</th>
            </tr></thead><tbody>${rows}</tbody>`);
        }
        else if (type === 'veriOrneklemi' && responseData.veriSetiOrneklem) {
            document.getElementById('dsTitle').textContent = `Rastgele Seçilen Örneklem (${responseData.veriSetiOrneklem.length} birim)`;
            let rows = responseData.veriSetiOrneklem.map((v, i) => `<tr><td style="padding:8px; border-bottom:1px solid #444">${i+1}</td><td style="padding:8px; border-bottom:1px solid #444">${v}</td></tr>`).join('');
            htmlOut = buildTable(`<thead><tr><th style="padding:8px; border-bottom:1px solid #555">Sıra</th><th style="padding:8px; border-bottom:1px solid #555">Değer</th></tr></thead><tbody>${rows}</tbody>`);
        }

        container.innerHTML = htmlOut;
        resultDiv.style.display = 'block';

    } catch(e) {
        showError('dsError', e.message);
    }
}

// ----------------------------------------------------
// UI Render Helpers
// ----------------------------------------------------
function formatValue(val) {
    if (Number.isInteger(val)) return val.toString();
    return parseFloat(val.toFixed(4)).toString();
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

function renderTabakaliTable(tableId, rows) {
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

// ----------------------------------------------------
// Initialization Filter Events
// ----------------------------------------------------
document.addEventListener('DOMContentLoaded', () => {
    // Sadece numeric giriş filtreleri için
    const intKeys = ['brN', 'sysBuyukN', 'sysKucukN', 'tabBuyukN', 'tabKucukN', 'tabSayisi', 'dsSampleN', 'dsClassCount'];
    intKeys.forEach(id => {
        const el = document.getElementById(id);
        if(!el) return;
        el.addEventListener('input', function() {
             this.value = this.value.replace(/[^0-9]/g, '');
        });
    });
});
