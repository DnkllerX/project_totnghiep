/**
 * Thanh do do manh mat khau - CHI mang tinh hien thi/goi y, KHONG chan submit form du diem thap.
 * Goi initPasswordStrengthMeter('idCuaOInput', 'idCuaThanhBar', 'idCuaNhanChu') sau khi DOM san sang.
 */
function calculatePasswordStrength(password) {
    if (!password) return 0;
    let score = 0;
    if (password.length >= 8) score++;
    if (password.length >= 12) score++;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) score++;
    if (/[0-9]/.test(password)) score++;
    if (/[^A-Za-z0-9]/.test(password)) score++;
    return Math.min(score, 4); // 0-4
}

const PASSWORD_STRENGTH_LABELS = ['Rat yeu', 'Yeu', 'Trung binh', 'Kha', 'Manh'];
const PASSWORD_STRENGTH_COLORS = ['#7f1d1d', '#b45309', '#a16207', '#15803d', '#166534'];

function initPasswordStrengthMeter(inputId, barId, labelId) {
    const input = document.getElementById(inputId);
    const bar = document.getElementById(barId);
    const label = document.getElementById(labelId);
    if (!input || !bar || !label) return;

    input.addEventListener('input', () => {
        const score = calculatePasswordStrength(input.value);
        const pct = input.value ? (score + 1) * 20 : 0;
        bar.style.width = pct + '%';
        bar.style.background = PASSWORD_STRENGTH_COLORS[score];
        label.textContent = input.value ? PASSWORD_STRENGTH_LABELS[score] : '';
    });
}
