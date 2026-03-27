import { createI18n } from 'vue-i18n';
import bg from './locales/bg.json';
import en from './locales/en.json';

const savedLocale = localStorage.getItem('user-locale') || 'bg';

const i18n = createI18n({
<<<<<<< HEAD
  legacy: false,
  locale: savedLocale,
=======
  legacy: false, 
  locale: en,
>>>>>>> origin/main
  fallbackLocale: 'bg',
  messages: {
    bg,
    en
  }
});
export default i18n;