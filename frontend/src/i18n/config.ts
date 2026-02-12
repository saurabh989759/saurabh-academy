import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'

i18n.use(initReactI18next).init({
  resources: {
    en: {
      translation: {
        app: {
          name: 'Academy Management System',
        },
        nav: {
          home: 'Home',
          students: 'Students',
          batches: 'Batches',
          login: 'Login',
          logout: 'Logout',
        },
        students: {
          title: 'Students',
          create: 'Create Student',
          edit: 'Edit Student',
          delete: 'Delete Student',
          name: 'Name',
          email: 'Email',
          graduationYear: 'Graduation Year',
          universityName: 'University',
          phoneNumber: 'Phone',
          batch: 'Batch',
        },
      },
    },
  },
  lng: 'en',
  fallbackLng: 'en',
  interpolation: {
    escapeValue: false,
  },
})

export default i18n

