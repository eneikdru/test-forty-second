import './app.css'
import LexiconFluxApp from './LexiconFluxApp.svelte'
import IntegrationsSettings from './components/IntegrationsSettings.svelte'

const params = new URLSearchParams(window.location.search);
const view = params.get('view');

let app;

if (view === 'settings') {
  app = new IntegrationsSettings({
    target: document.getElementById('app'),
  });
} else {
  app = new LexiconFluxApp({
    target: document.getElementById('app'),
  });
}

export default app
