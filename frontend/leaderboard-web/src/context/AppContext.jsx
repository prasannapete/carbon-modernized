import { createContext, useCallback, useContext, useRef } from 'react';

// The shared header has an "Events" dropdown with 10/20/30 buttons that, in the
// reference app, call a page-defined leadPagination(size). Its meaning differs per
// page (excel page length on the list, page size on the leaderboard), so pages
// register their own handler here and the header simply invokes it.
const AppContext = createContext(null);

export function AppProvider({ children }) {
  const leadPaginationRef = useRef(() => {});

  const registerLeadPagination = useCallback((fn) => {
    leadPaginationRef.current = fn || (() => {});
  }, []);

  const leadPagination = useCallback((size) => {
    leadPaginationRef.current(size);
  }, []);

  return (
    <AppContext.Provider value={{ registerLeadPagination, leadPagination }}>
      {children}
    </AppContext.Provider>
  );
}

export function useApp() {
  return useContext(AppContext);
}
