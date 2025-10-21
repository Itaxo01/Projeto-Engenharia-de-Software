class DisciplinasCache {
    constructor() {
        this.cacheKey = 'disciplinas_search_data';
        this.timestampKey = 'disciplinas_cache_timestamp';
        this.versionKey = 'disciplinas_cache_version';
        
        // Cache duration: 6 months (in milliseconds)
        this.cacheDuration = 6 * 30 * 24 * 60 * 60 * 1000; // 6 months
        
        // Version to force cache refresh when needed
        this.currentVersion = '1.0.3';
    }

    /**
     * Get disciplinas from cache or fetch from server
     */
    async getDisciplinas() {
        try {
            // Check if we have valid cached data
            const cachedData = this.getCachedData();
            if (cachedData) {
                return cachedData;
            }

            // Cache miss or expired - fetch from server
            const freshData = await this.fetchFromServer();
            // Store in cache
            this.setCachedData(freshData);
            
            return freshData;

        } catch (error) {
            const expiredCache = this.getCachedDataIgnoringExpiry();
            if (expiredCache) {
                return expiredCache;
            }
            throw error;
        }
    }

    /**
     * Get valid cached data (not expired)
     */
    getCachedData() {
        try {
            // Check version first
            const cachedVersion = localStorage.getItem(this.versionKey);
            if (cachedVersion !== this.currentVersion) {
                this.clearCache();
                return null;
            }

            // Check timestamp
            const timestamp = localStorage.getItem(this.timestampKey);
            if (!timestamp) {
                return null;
            }

            const cacheAge = Date.now() - parseInt(timestamp);
            if (cacheAge > this.cacheDuration) {
                this.clearCache();
                return null;
            }

            // Get data
            const cachedJson = localStorage.getItem(this.cacheKey);
            if (!cachedJson) {
                return null;
            }

            const data = JSON.parse(cachedJson);
            return data;

        } catch (error) {
            console.error('❌ Error reading cache:', error);
            this.clearCache();
            return null;
        }
    }

    /**
     * Get cached data ignoring expiry (for fallback)
     */
    getCachedDataIgnoringExpiry() {
        try {
            const cachedJson = localStorage.getItem(this.cacheKey);
            return cachedJson ? JSON.parse(cachedJson) : null;
        } catch (error) {
            return null;
        }
    }

    /**
     * Store data in cache
     */
    setCachedData(data) {
        try {
            localStorage.setItem(this.cacheKey, JSON.stringify(data));
            localStorage.setItem(this.timestampKey, Date.now().toString());
            localStorage.setItem(this.versionKey, this.currentVersion);
            
        } catch (error) {
            console.error('❌ Error saving to cache:', error);
            
            // Handle quota exceeded error
            if (error.name === 'QuotaExceededError') {
                console.warn('💾 LocalStorage quota exceeded, clearing old data');
                this.clearCache();
                
                // Try again with clean cache
                try {
                    localStorage.setItem(this.cacheKey, JSON.stringify(data));
                    localStorage.setItem(this.timestampKey, Date.now().toString());
                    localStorage.setItem(this.versionKey, this.currentVersion);
                } catch (retryError) {
                    console.error('❌ Failed to cache even after clearing:', retryError);
                }
            }
        }
    }

    /**
     * Fetch fresh data from server
     */
    async fetchFromServer() {
        const response = await fetch('/api/search/disciplinas', {
            headers: {
                'Cache-Control': 'no-cache',
                'Pragma': 'no-cache'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        const data = await response.json();
        console.log(`🌐 Fetched ${data.length} disciplinas from server`);
        
        return data;
    }

    /**
     * Force refresh cache
     */
    async forceRefresh() {
        console.log('🔄 Forcing cache refresh...');
        this.clearCache();
        return await this.getDisciplinas();
    }

    /**
     * Clear cache
     */
    clearCache() {
        localStorage.removeItem(this.cacheKey);
        localStorage.removeItem(this.timestampKey);
        localStorage.removeItem(this.versionKey);
        console.log('🗑️ Cache cleared');
    }

    /**
     * Get cache info for debugging
     */
    getCacheInfo() {
        const timestamp = localStorage.getItem(this.timestampKey);
        const version = localStorage.getItem(this.versionKey);
        const hasData = !!localStorage.getItem(this.cacheKey);
        
        if (!timestamp || !hasData) {
            return { cached: false };
        }

        const age = Date.now() - parseInt(timestamp);
        const isExpired = age > this.cacheDuration;

        return {
            cached: true,
            version: version,
            age: age,
            ageMinutes: Math.round(age / 1000 / 60),
            ageDays: Math.round(age / 1000 / 60 / 60 / 24),
            isExpired: isExpired,
            expiresIn: this.cacheDuration - age
        };
    }

    /**
     * Check if localStorage is available
     */
    static isStorageAvailable() {
        try {
            const test = 'localStorage_test';
            localStorage.setItem(test, test);
            localStorage.removeItem(test);
            return true;
        } catch (e) {
            return false;
        }
    }
}

// Global cache instance
const disciplinasCache = new DisciplinasCache();





let searchTimeout;
const searchInput = document.getElementById('searchInput');
const searchResults = document.getElementById('searchResults');

let disciplinas_fetch = null;
let fuse = null;
let isLoading = false;
let selectedIndex = -1;


document.addEventListener('DOMContentLoaded', async function() {
    await loadDisciplinasData();
});

searchInput.addEventListener('focus', async function() {
    if (!disciplinas_fetch && !isLoading) {
        await loadDisciplinasData();
    }
});

searchInput.addEventListener('input', function() {
    const query = this.value.trim();
    
	 clearTimeout(searchTimeout);

    if (query.length < 2) {
        searchResults.innerHTML = '';
        searchResults.style.display = 'none';
		  selectedIndex = -1;
        return;
    }

	 searchTimeout = setTimeout(() => {
		 performSearch(query);
	 }, 150);
});

function performSearch(query) {
    if (!fuse || query.trim().length < 1) return;
    
    // Perform fuzzy search with Fuse.js
    const fuseResults = fuse.search(query, {
        limit: 20  // Limit results for better performance
    });
    
    // Transform Fuse results to our format
    const results = fuseResults.map(result => ({
        ...result.item,              // Original disciplina data
        fuseScore: result.score,     // Fuse match score (lower = better)
    }));
    
    displayResults(results, query);
}

async function loadDisciplinasData() {
    if (isLoading) return;
    
    isLoading = true;
    
    try {
        // Check if localStorage is supported
        if (!DisciplinasCache.isStorageAvailable()) {
            disciplinas_fetch = await fetchDisciplinasDirectly();
        } else {
            // Use cached data
            disciplinas_fetch = await disciplinasCache.getDisciplinas();
        }
        
        if (disciplinas_fetch && disciplinas_fetch.length > 0) {
            initializeFuseJS();
            if (window.location.hostname === 'localhost') {
                console.table(disciplinasCache.getCacheInfo());
            }
        } else {
            console.error('❌ No disciplinas data loaded');
        }
        
    } catch (error) {
        console.error('❌ Error loading disciplinas:', error);
        showError('Erro ao carregar disciplinas');
    } finally {
        isLoading = false;
    }
}


async function fetchDisciplinasDirectly() {
    const response = await fetch('/api/search/disciplinas');
    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
    }
    return await response.json();
}

async function refreshDisciplinasData() {
    try {
        disciplinas_fetch = await disciplinasCache.forceRefresh();
        initializeFuseJS();
        
        return true;
    } catch (error) {
        console.error('❌ Error refreshing data:', error);
        return false;
    }
}


function initializeFuseJS() {
    const fuseOptions = {
        // Fields to search in
        keys: [
            {
                name: 'codigo',
                weight: 2    // Higher weight = more important
            },
            {
                name: 'nome',
                weight: 1
            }
        ],
        // Search configuration
        threshold: 0.4,              // 0.0 = exact match, 1.0 = match anything
        distance: 100,               // How far from the start of string to search
        minMatchCharLength: 1,       // Minimum character length to trigger search
        ignoreLocation: true,        // Search entire string, not just from start
        includeScore: true,          // Include match score in results
        includeMatches: true,        // Include match information for highlighting
        shouldSort: true,            // Sort results by relevance
        findAllMatches: false,       // Stop after first match per field
        
        // Advanced options
        isCaseSensitive: false,
        ignoreFieldNorm: false,
        fieldNormWeight: 1
    };

    // Create Fuse instance
    fuse = new Fuse(disciplinas_fetch, fuseOptions);
}

function displayResults(results) {
    // Clear any existing selection
    const existingSelected = searchResults.querySelector('.search-item.selected');
    if (existingSelected) {
        existingSelected.classList.remove('selected');
    }
    
    if (!results || results.length === 0) {
        searchResults.innerHTML = '<div class="search-item no-results">Nenhuma disciplina encontrada</div>';
        searchResults.style.display = 'block';
        return;
    }
    
    const html = results.map((result) => {
        // Use Fuse.js match information for better highlighting
        return `
            <div class="search-item" onclick="goToDisciplina('${result.codigo}')" data-codigo="${result.codigo}">
                <div class="search-item-code">${result.codigo}</div>
                <div class="search-item-name">${result.nome}</div>
            </div>
        `;
    }).join('');
    
    searchResults.innerHTML = html;
    searchResults.style.display = 'block';
}

function showLoading() {
    searchResults.innerHTML = '<div class="search-item loading">🔄 Carregando disciplinas...</div>';
    searchResults.style.display = 'block';
}

function hideLoading() {
    if (searchResults.innerHTML.includes('Carregando')) {
        searchResults.style.display = 'none';
    }
}

function showError(message) {
    searchResults.innerHTML = `<div class="search-item error">❌ ${message}</div>`;
    searchResults.style.display = 'block';
}

function goToDisciplina(codigo) {
    window.location.href = `/class/${codigo}`;
}

// Fecha resultados ao clicar fora
document.addEventListener('click', function(e) {
    if (!e.target.closest('.search-container')) {
        searchResults.style.display = 'none';
    }
});

// Previne fechar ao clicar dentro dos resultados
searchResults.addEventListener('click', function(e) {
    e.stopPropagation();
});



function getSimpleScore(query, target) {
    if (query === target) return 1.0;
    if (target.startsWith(query)) return 0.8;
    if (target.includes(query)) return 0.6;
    return 0.0;
}


// Keyboard navigation
searchInput.addEventListener('keydown', function(e) {
    const items = searchResults.querySelectorAll('.search-item:not(.no-results):not(.loading):not(.error)');
    
    if (items.length === 0) return;
    
    switch(e.key) {
        case 'ArrowDown':
            e.preventDefault();
            navigateDown(items);
            break;
            
        case 'ArrowUp':
            e.preventDefault();
            navigateUp(items);
            break;
            
        case 'Enter':
            e.preventDefault();
            selectCurrentItem(items);
            break;
            
        case 'Escape':
            e.preventDefault();
            closeResults();
            break;
            
        case 'Tab':
            // Allow tabbing but close results
            closeResults();
            break;
    }
});

function navigateDown(items) {
    if (selectedIndex >= 0 && selectedIndex < items.length) {
        items[selectedIndex].classList.remove('selected');
    }
    
    selectedIndex = selectedIndex < items.length - 1 ? selectedIndex + 1 : 0;
    items[selectedIndex].classList.add('selected');
    scrollToSelectedItem(items[selectedIndex]);
}

function navigateUp(items) {
    if (selectedIndex >= 0 && selectedIndex < items.length) {
        items[selectedIndex].classList.remove('selected');
    }

	 selectedIndex = selectedIndex > 0 ? selectedIndex - 1 : items.length - 1;
    items[selectedIndex].classList.add('selected');
    scrollToSelectedItem(items[selectedIndex]);
}

function selectCurrentItem(items) {
    if (selectedIndex >= 0 && selectedIndex < items.length) {
        const selectedItem = items[selectedIndex];
        const codigo = selectedItem.getAttribute('data-codigo');
        
        console.log(`✅ Selected disciplina: ${codigo}`);
        
        // Add visual feedback before navigation
        selectedItem.style.transform = 'scale(0.98)';
        setTimeout(() => {
            goToDisciplina(codigo);
        }, 100);
    }
}

function scrollToSelectedItem(item) {
    const container = searchResults;
    const itemRect = item.getBoundingClientRect();
    const containerRect = container.getBoundingClientRect();
    
    // Check if item is outside visible area
    if (itemRect.top < containerRect.top) {
        // Item is above visible area
        item.scrollIntoView({ behavior: 'smooth', block: 'start' });
    } else if (itemRect.bottom > containerRect.bottom) {
        // Item is below visible area
        item.scrollIntoView({ behavior: 'smooth', block: 'end' });
    }
}

function closeResults() {
    searchResults.style.display = 'none';
    selectedIndex = -1;
    searchInput.blur();
}

// Handle mouse hover for keyboard navigation consistency
searchResults.addEventListener('mouseover', function(e) {
    if (e.target.classList.contains('search-item') && !e.target.classList.contains('no-results')) {
        // Remove previous selection
        const selected = searchResults.querySelector('.search-item.selected');
        if (selected) selected.classList.remove('selected');
        
        // Add selection to hovered item
        e.target.classList.add('selected');
    }
});