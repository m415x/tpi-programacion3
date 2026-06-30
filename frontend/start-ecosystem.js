import { spawn, exec } from 'child_process';
import path from 'path';
import { fileURLToPath } from 'url';
import http from 'http';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

// CONFIGURACIÓN DE RUTAS RELATIVAS
const BACKEND_DIR = path.join(__dirname, '../backend');
const FRONTEND_DIR = __dirname;

console.log('Iniciando el ecosistema de Food Store...\n');

// 1. ARRANCAR BACKEND (Gradle en Windows)
console.log('Arrancando servidor Spring Boot con Gradle (Puerto 8008)...');

// En Windows ejecutamos "gradlew.bat" directamente sin el "./"
const isWin = process.platform === 'win32';
const backendCommand = isWin ? 'cmd.exe' : './gradlew';
const backendArgs = isWin ? ['/c', 'gradlew.bat', 'bootRun'] : ['bootRun'];

const backendProcess = spawn(backendCommand, backendArgs, {
    cwd: BACKEND_DIR,
    shell: false,
    stdio: 'inherit'
});

// 2. ARRANCAR FRONTEND (Vite)
console.log('Arrancando servidor de desarrollo Vite (Puerto 5173)...');
const frontendProcess = spawn('npm', ['run', 'dev'], {
    cwd: FRONTEND_DIR,
    shell: true,
    stdio: 'inherit'
});

// FUNCIÓN DE SONDEO (POLLING)
// Realiza peticiones HTTP al backend hasta que este responda, garantizando que esté activo.
function checkBackendReady(url, callback) {
    const request = http.get(url, (res) => {
        // Si el servidor responde con cualquier status code, es porque el puerto ya está escuchando
        callback();
    });

    request.on('error', () => {
        // Si falla (ECONNREFUSED), el backend sigue compilando. Reintentamos en 1 segundo.
        setTimeout(() => checkBackendReady(url, callback), 1000);
    });
}

// 3. ESPERAR ACTIVAMENTE AL BACKEND ANTES DE ABRIR EL NAVEGADOR
console.log('\nEsperando que Spring Boot termine de compilar e iniciar...');
checkBackendReady('http://localhost:8008/products', () => {
    const url = 'http://localhost:5173';
    console.log(`\n¡Servidor Spring Boot Detectado Activo! Abriendo navegador en ${url}...`);

    const startCmd = process.platform === 'win32' ? 'start' : process.platform === 'darwin' ? 'open' : 'xdg-open';
    exec(`${startCmd} ${url}`);
});

// Manejo de cierre limpio
process.on('SIGINT', () => {
    console.log('\nApagando servicios...');
    backendProcess.kill();
    frontendProcess.kill();
    process.exit();
});