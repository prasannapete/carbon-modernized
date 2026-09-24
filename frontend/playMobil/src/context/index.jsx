import { createContext, useContext, useState } from "react";

const GlobalContext = createContext(null);

export const GlobalContextProvider = ({ children }) => {
    const [selectedCandidates, setSelectedCandidates] = useState([]);
    const [isTableHeaderChecked, setIsTableHeaderChecked] = useState(false);
    const handleTableHeaderCheckbox = (payload) => {
        setIsTableHeaderChecked(payload);
        if (!payload) {
            setSelectedCandidates([]);
        }
    }
    const [allCombinedTemplates, setAllCombinedTemplates] = useState([]);
    const [privateUserId, setPrivateUserId] = useState("");

    return (
        <GlobalContext.Provider value={{
            isTableHeaderChecked, handleTableHeaderCheckbox,
            selectedCandidates, setSelectedCandidates,
            allCombinedTemplates, setAllCombinedTemplates,
            privateUserId, setPrivateUserId
        }}>
            {children}
        </GlobalContext.Provider>
    );
};

export const useGlobalContext = () => {
    const context = useContext(GlobalContext);
    if (!context) throw new Error("Context must be used within its provider");
    return context;
};