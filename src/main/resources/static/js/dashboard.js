// Estado
let semestres = [];
let semestreAtual = null;

// Inicializar
document.addEventListener('DOMContentLoaded', () => {
    carregarDados();
    setupModal();
});

// Carregar dados do servidor
async function carregarDados() {
    try {
        const response = await fetch('/api/mapa/listar');
        if (!response.ok) {
            throw new Error('Erro ao carregar dados');
        }
        
        const dados = await response.json();
        
        // Encontrar o maior número de semestre nos dados
        let maxSemestre = 8; // Mínimo padrão
        dados.forEach(item => {
            if (item.semestre > maxSemestre) {
                maxSemestre = item.semestre;
            }
        });
        
        // Inicializar semestres vazios até o máximo encontrado
        for (let i = 1; i <= maxSemestre; i++) {
            semestres.push({ numero: i, disciplinas: [] });
        }
        
        // Preencher com dados do banco
        dados.forEach(item => {
            const semestreIdx = item.semestre - 1;
            if (semestreIdx >= 0 && semestreIdx < semestres.length) {
                semestres[semestreIdx].disciplinas.push({
                    codigo: item.codigo,
                    nome: item.nome,
                    avaliada: item.avaliada
                });
            }
        });
        
        renderizar();
    } catch (error) {
        console.error('Erro ao carregar mapa curricular:', error);
        // Inicializar vazio em caso de erro (8 semestres padrão)
        for (let i = 1; i <= 8; i++) {
            semestres.push({ numero: i, disciplinas: [] });
        }
        renderizar();
    }
}

// Renderizar
function renderizar() {
    const html = semestres.map((s, i) => `
        <div class="semestre-row">
            <div class="fase-label">Fase ${s.numero}</div>
            ${s.disciplinas.map(d => `
                <div class="disciplina-card ${d.avaliada ? 'avaliada' : 'nao-avaliada'}">
                    <button class="btn-remover-disciplina" onclick="removerDisciplina(event, ${i}, '${d.codigo}')" title="Remover disciplina">×</button>
                    <div onclick="window.location.href='/class/${d.codigo}'" style="cursor: pointer;">
                        <div class="disciplina-codigo">${d.codigo}</div>
                        <div class="disciplina-nome">${d.nome}</div>
                    </div>
                </div>
            `).join('')}
            ${s.disciplinas.length < 6 ? `<div class="btn-add-disciplina-card" onclick="abrirModal(${i})">+</div>` : ''}
        </div>
    `).join('');
    document.getElementById('semestresContainer').innerHTML = html;
}

// Remover disciplina
async function removerDisciplina(event, semestreIdx, codigo) {
    event.stopPropagation();
    
    if (!confirm(`Deseja remover a disciplina ${codigo}?`)) {
        return;
    }
    
    try {
        const response = await fetch(`/api/mapa/remover/${codigo}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error('Erro ao remover disciplina');
        }
        
        // Remover localmente
        semestres[semestreIdx].disciplinas = semestres[semestreIdx].disciplinas.filter(d => d.codigo !== codigo);
        renderizar();
    } catch (error) {
        console.error('Erro ao remover disciplina:', error);
        alert('Erro ao remover disciplina. Tente novamente.');
    }
}

// Adicionar semestre
function adicionarSemestre() {
    semestres.push({ numero: semestres.length + 1, disciplinas: [] });
    renderizar();
}

// Upload placeholder
function uploadCurriculo() {
    alert('Em desenvolvimento!');
}

// Modal
function setupModal() {
    const input = document.getElementById('modalSearchInput');
    const results = document.getElementById('modalSearchResults');
    
    document.getElementById('modalAddDisciplina').onclick = e => {
        if (e.target.id === 'modalAddDisciplina') fecharModal();
    };
    
    document.addEventListener('keydown', e => {
        if (e.key === 'Escape') fecharModal();
    });
    
    let timeout;
    input.oninput = function() {
        clearTimeout(timeout);
        const termo = this.value.trim();
        if (termo.length < 2) {
            results.innerHTML = '';
            results.classList.remove('show');
            return;
        }
        timeout = setTimeout(() => buscar(termo), 200);
    };
}

function abrirModal(idx) {
    semestreAtual = idx;
    document.getElementById('modalSearchInput').value = '';
    document.getElementById('modalSearchResults').innerHTML = '';
    document.getElementById('modalAddDisciplina').classList.add('show');
    setTimeout(() => document.getElementById('modalSearchInput').focus(), 100);
}

function fecharModal() {
    document.getElementById('modalAddDisciplina').classList.remove('show');
    semestreAtual = null;
}

function buscar(termo) {
    const results = document.getElementById('modalSearchResults');
    const dados = window.searchDisciplinas(termo);
    
    if (!dados || dados.length === 0) {
        results.innerHTML = '<div class="modal-search-item no-results">Nenhuma encontrada</div>';
    } else {
        results.innerHTML = dados.slice(0,5).map(d => `
            <div class="modal-search-item" onclick="adicionar('${d.codigo}','${d.nome.replace(/'/g,"\\'")}')">
                <div class="modal-search-item-code">${d.codigo}</div>
                <div class="modal-search-item-name">${d.nome}</div>
            </div>
        `).join('');
    }
    results.classList.add('show');
}

function adicionar(cod, nome) {
    adicionarNoServidor(cod, nome);
}

async function adicionarNoServidor(cod, nome) {
    try {
        const response = await fetch('/api/mapa/adicionar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                disciplinaId: cod,
                semestre: semestreAtual + 1 // Backend espera 1-indexed
            })
        });
        
        if (!response.ok) {
            throw new Error('Erro ao adicionar disciplina');
        }
        
        // Adicionar localmente
        semestres[semestreAtual].disciplinas.push({ 
            codigo: cod, 
            nome: nome, 
            avaliada: false 
        });
        
        renderizar();
        fecharModal();
    } catch (error) {
        console.error('Erro ao adicionar disciplina:', error);
        alert('Erro ao adicionar disciplina. Tente novamente.');
    }
}
