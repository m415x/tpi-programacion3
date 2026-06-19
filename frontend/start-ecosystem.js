import { spawn, exec } from 'child_process';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

// 📝 CONFIGURACIÓN DE RUTAS RELATIVAS
const BACKEND_DIR = path.join(__dirname, '../backend');
const FRONTEND_DIR = __dirname;

console.log('🚀 Iniciando el ecosistema de Food Store (Entorno Windows)...\n');

// 1. ARRANCAR BACKEND (Gradle en Windows)
console.log('⏳ Arrancando servidor Spring Boot con Gradle (Puerto 8008)...');

// En Windows ejecutamos "gradlew.bat" directamente sin el "./"
const backendCommand = process.platform === 'win32' ? 'gradlew.bat' : './gradlew';

const backendProcess = spawn(backendCommand, ['bootRun'], {
    cwd: BACKEND_DIR,
    shell: true,
    stdio: 'inherit'
});

// 2. ARRANCAR FRONTEND (Vite)
console.log('⏳ Arrancando servidor de desarrollo Vite (Puerto 5173)...');
const frontendProcess = spawn('npm', ['run', 'dev'], {
    cwd: FRONTEND_DIR,
    shell: true,
    stdio: 'inherit'
});

// 3. ESPERAR UN MOMENTO Y ABRIR EL NAVEGADOR AUTOMÁTICAMENTE
// Le damos 6 segundos a Gradle porque la primera compilación en frío suele tardar un poquito más
setTimeout(() => {
    const url = 'http://localhost:5173';
    console.log(`\n🌐 Abriendo navegador en ${url}...`);

    const startCmd = process.platform === 'win32' ? 'start' : process.platform === 'darwin' ? 'open' : 'xdg-open';
    exec(`${startCmd} ${url}`);
}, 15000);

// Manejo de cierre limpio
process.on('SIGINT', () => {
    console.log('\n🛑 Apagando servicios...');
    backendProcess.kill();
    frontendProcess.kill();
    process.exit();
});