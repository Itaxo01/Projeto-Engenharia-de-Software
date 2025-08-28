document.getElementById('uploadForm')?.addEventListener('submit', (e) => {
  const fileInput = document.getElementById('file');
  if (!fileInput || !fileInput.files || fileInput.files.length === 0) {
    e.preventDefault();
    alert('Please choose a PDF file first.');
  }
});
