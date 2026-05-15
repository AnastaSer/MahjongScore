// ---------- Глобальные переменные ----------
let combinationCount = 0;

// ---------- Вспомогательные функции ----------
function getSelectedValues(name) {
    return Array.from(document.querySelectorAll(`input[name="${name}"]:checked`))
        .map(cb => cb.value);
}

function createCombinationBlock() {
    const blockId = `comb_${combinationCount++}`;
    const div = document.createElement('div');
    div.className = 'combination-block';
    div.id = blockId;

    div.innerHTML = `
        <div class="comb-row">
            <select class="comb-type">
                <option value="открытая">Открытая</option>
                <option value="закрытая">Закрытая</option>
            </select>
            <select class="comb-element">
                <option value="пара">Пара</option>
                <option value="тройка">Тройка</option>
                <option value="четверка">Четверка</option>
                <option value="последовательность три">Последовательность 3</option>
                <option value="последовательность четыре">Последовательность 4</option>
                <option value="один">Одиночная</option>
            </select>
            <select class="comb-suit">
                <option value="символ">Символ</option>
                <option value="дот">Дот</option>
                <option value="бамбук">Бамбук</option>
                <option value="дракон">Дракон</option>
                <option value="ветер">Ветер</option>
                <option value="бонус цветок">Бонус Цветок</option>
                <option value="бонус сезон">Бонус Сезон</option>
            </select>
            <select class="comb-value">
                <option value="">---</option>
            </select>
            <button type="button" class="remove-comb">✖</button>
        </div>
    `;
    
    // Применяем скрытие опции в зависимости от текущих правил
    const rulesSelect = document.getElementById('rules');
    const sequenceFourOption = div.querySelector('.comb-element option[value="последовательность четыре"]');
    if (sequenceFourOption && rulesSelect.value !== 'родители') {
        sequenceFourOption.style.display = 'none';
        // если вдруг она выбрана (при дублировании), сбрасываем
        if (div.querySelector('.comb-element').value === 'последовательность четыре') {
            div.querySelector('.comb-element').value = 'последовательность три';
        }
    }
    
    const suitSelect = div.querySelector('.comb-suit');
    const valueSelect = div.querySelector('.comb-value');
    const elementSelect = div.querySelector('.comb-element');

    function updateValueOptions() {
        const suit = suitSelect.value;
        const element = elementSelect.value;
        let options = [];

        if (suit === 'дракон') {
            options = ['красный', 'зеленый', 'белый'];
        } else if (suit === 'ветер') {
            options = ['восточный', 'южный', 'западный', 'северный'];
        } else if (['бонус цветок', 'бонус сезон'].includes(suit)) {
                options = Array.from({ length: 4 }, (_, i) => (i + 1).toString());
        } else if (['символ', 'дот', 'бамбук'].includes(suit)) {
            if (element === 'последовательность три') {
                options = Array.from({ length: 7 }, (_, i) => (i + 1).toString());
            } else if (element === 'последовательность четыре') {
                options = Array.from({ length: 6 }, (_, i) => (i + 1).toString());
            } else {
                options = Array.from({ length: 9 }, (_, i) => (i + 1).toString());
            }
        }

        valueSelect.innerHTML = options.map(v => `<option value="${v}">${v}</option>`).join('');
        valueSelect.disabled = options.length === 0;
        if (options.length === 0) valueSelect.style.display = 'none';
        else valueSelect.style.display = 'inline-block';
    }

    suitSelect.addEventListener('change', updateValueOptions);
    elementSelect.addEventListener('change', updateValueOptions);
    updateValueOptions();

    div.querySelector('.remove-comb').addEventListener('click', () => div.remove());
    return div;
}

function collectCombinations() {
    const blocks = document.querySelectorAll('.combination-block');
    const result = [];
    blocks.forEach(block => {
        const type = block.querySelector('.comb-type').value;
        const element = block.querySelector('.comb-element').value;
        const suit = block.querySelector('.comb-suit').value;
        const value = block.querySelector('.comb-value').value;
        let parts = [type, element, suit, value];
        result.push(parts.filter(p => p && p !== '---').join(' '));
    });
    return result;
}

// ---------- Инициализация формы ----------
document.getElementById('addCombinationBtn').addEventListener('click', () => {
    document.getElementById('combinationsContainer').appendChild(createCombinationBlock());
    updateCombinationOptions();
});

document.getElementById('mahjongCheckbox').addEventListener('change', (e) => {
    document.getElementById('mahjongFlags').style.display = e.target.checked ? 'block' : 'none';
});

document.getElementById('scoreForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const playerWind = document.getElementById('playerWind').value;
    const vipWind = document.getElementById('vipWind').value;
    const rules = document.getElementById('rules').value;
    const combinations = collectCombinations();
    
    // Собираем флаги
    let flags = getSelectedValues('commonFlag');
    if (document.getElementById('mahjongCheckbox').checked) {
        flags.push('маджонг');
        flags.push(...getSelectedValues('mahjongFlag'));
    }

    const requestBody = {
        playerWind: playerWind,
        vipWind: vipWind,
        combinationsOrTiles: combinations,
        flags: flags,
        rules: rules
    };

    console.log('Отправляю JSON:', JSON.stringify(requestBody));

    fetch('http://localhost:8080/api/mahjong_scores/calculate_one_hand', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody)
    })
    .then(async response => {
        const text = await response.text();
        let data;
        try { data = JSON.parse(text); } catch(e) { data = { raw: text }; }
        if (!response.ok) {
            const errMsg = data.body?.detail || data.detail || data.message || text;
            throw new Error(errMsg);
        }
        return data;
    })
    .then(data => {
    document.getElementById('score').textContent = data.score;
    document.getElementById('isMahjong').textContent = data.isMahjong ? 'Да' : 'Нет';
    
    // Отображаем применённые флаги
    const appliedFlagsContainer = document.getElementById('appliedFlags');
    if (appliedFlagsContainer && data.appliedFlags && data.appliedFlags.length > 0) {
        const flagsList = data.appliedFlags.map(flag => `<li>${flag}</li>`).join('');
        appliedFlagsContainer.innerHTML = `<ul>${flagsList}</ul>`;
        appliedFlagsContainer.style.display = 'block';
    } else if (appliedFlagsContainer) {
        appliedFlagsContainer.style.display = 'none';
    }
    
    document.getElementById('result').style.display = 'block';
    document.getElementById('error').style.display = 'none';
    })
    .catch(err => {
        document.getElementById('errorMessage').textContent = err.message;
        document.getElementById('error').style.display = 'block';
        document.getElementById('result').style.display = 'none';
    });
});

// Следить за изменением правил
function updateCombinationOptions() {
    const rulesSelect = document.getElementById('rules');
    const selectedRules = rulesSelect.value;
    const allCombBlocks = document.querySelectorAll('.combination-block');
    
    allCombBlocks.forEach(block => {
        const elementSelect = block.querySelector('.comb-element');
        const sequenceFourOption = elementSelect.querySelector('option[value="последовательность четыре"]');
        
        if (sequenceFourOption) {
            // Показываем опцию только для правил родителей
            if (selectedRules === 'родители') {
                sequenceFourOption.style.display = 'block';
            } else {
                sequenceFourOption.style.display = 'none';
                // Если сейчас выбрана "последовательность четыре", сбрасываем на "последовательность три"
                if (elementSelect.value === 'последовательность четыре') {
                    elementSelect.value = 'последовательность три';
                }
            }
        }
    });
}

// Добавляем слушатель на изменение правил
document.getElementById('rules').addEventListener('change', updateCombinationOptions);

// Добавляем первую пустую комбинацию для примера
document.getElementById('addCombinationBtn').click();

updateCombinationOptions();
