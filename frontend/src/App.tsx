import { Routes, Route, Navigate } from 'react-router-dom'
import { Toaster } from './components/ui/Toaster'
import Navbar from './components/layout/Navbar'
import Home from './pages/Home'
import StudentsList from './pages/StudentsList'
import StudentDetail from './pages/StudentDetail'
import CreateStudent from './pages/CreateStudent'
import EditStudent from './pages/EditStudent'
import BatchesList from './pages/BatchesList'
import BatchDetail from './pages/BatchDetail'
import CreateBatch from './pages/CreateBatch'
import EditBatch from './pages/EditBatch'
import ClassesList from './pages/ClassesList'
import ClassDetail from './pages/ClassDetail'
import CreateClass from './pages/CreateClass'
import EditClass from './pages/EditClass'
import MentorsList from './pages/MentorsList'
import MentorDetail from './pages/MentorDetail'
import CreateMentor from './pages/CreateMentor'
import EditMentor from './pages/EditMentor'
import MentorSessionsList from './pages/MentorSessionsList'
import CreateMentorSession from './pages/CreateMentorSession'
import MentorSessionDetail from './pages/MentorSessionDetail'
import EditMentorSession from './pages/EditMentorSession'
import Login from './pages/Login'
import { AuthProvider } from './context/AuthContext'
import { RealtimeProvider } from './context/RealtimeContext'
import { ProtectedRoute } from './components/auth/ProtectedRoute'

function App() {
  return (
    <AuthProvider>
      <RealtimeProvider>
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
          <Navbar />
          <main className="container mx-auto px-4 py-8">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route
                path="/"
                element={
                  <ProtectedRoute>
                    <Home />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/students"
                element={
                  <ProtectedRoute>
                    <StudentsList />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/students/new"
                element={
                  <ProtectedRoute>
                    <CreateStudent />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/students/:id"
                element={
                  <ProtectedRoute>
                    <StudentDetail />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/students/:id/edit"
                element={
                  <ProtectedRoute>
                    <EditStudent />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/batches"
                element={
                  <ProtectedRoute>
                    <BatchesList />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/batches/new"
                element={
                  <ProtectedRoute>
                    <CreateBatch />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/batches/:id"
                element={
                  <ProtectedRoute>
                    <BatchDetail />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/batches/:id/edit"
                element={
                  <ProtectedRoute>
                    <EditBatch />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/classes"
                element={
                  <ProtectedRoute>
                    <ClassesList />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/classes/new"
                element={
                  <ProtectedRoute>
                    <CreateClass />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/classes/:id"
                element={
                  <ProtectedRoute>
                    <ClassDetail />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/classes/:id/edit"
                element={
                  <ProtectedRoute>
                    <EditClass />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/mentors"
                element={
                  <ProtectedRoute>
                    <MentorsList />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/mentors/new"
                element={
                  <ProtectedRoute>
                    <CreateMentor />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/mentors/:id"
                element={
                  <ProtectedRoute>
                    <MentorDetail />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/mentors/:id/edit"
                element={
                  <ProtectedRoute>
                    <EditMentor />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/mentor-sessions"
                element={
                  <ProtectedRoute>
                    <MentorSessionsList />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/mentor-sessions/new"
                element={
                  <ProtectedRoute>
                    <CreateMentorSession />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/mentor-sessions/:id"
                element={
                  <ProtectedRoute>
                    <MentorSessionDetail />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/mentor-sessions/:id/edit"
                element={
                  <ProtectedRoute>
                    <EditMentorSession />
                  </ProtectedRoute>
                }
              />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
          <Toaster />
        </div>
      </RealtimeProvider>
    </AuthProvider>
  )
}

export default App

