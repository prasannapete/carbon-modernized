import React, { useState } from "react";

const CountrySelector = ({ data, value, onChange }) => {
    const countries = data.filter(c => c.value !== "all"); // exclude the 'all' option
    const allOption = data.find(c => c.value === "all"); // "All Countries" object

    const [search, setSearch] = useState("");

    const handleSelect = (countryValue) => {
        if (countryValue === "all") {
            // If "All Countries" clicked
            if (value.length === countries.length) {
                // All are selected → unselect all
                onChange([]);
            } else {
                // Select all
                onChange(countries.map((c) => c.value));
            }
            return;
        }

        // Individual country clicked
        let updated = [...value];
        if (value.length === countries.length) {
            // All selected → click one → select only that one
            updated = [countryValue];
        } else {
            if (updated.includes(countryValue)) {
                updated = updated.filter((v) => v !== countryValue);
            } else {
                updated.push(countryValue);
            }
        }

        onChange(updated);
    };

    // Filter & sort countries: selected first
    const filteredCountries = countries
        .filter((c) => c.label.toLowerCase().includes(search.toLowerCase()))
        .sort((a, b) => {
            if (value.includes(a.value) && !value.includes(b.value)) return -1;
            if (!value.includes(a.value) && value.includes(b.value)) return 1;
            return 0;
        });

    return (
        <div style={{ padding: "10px", width: "100%" }}>
            {/* Search input */}
            <input
                type="text"
                placeholder="Search country..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                style={{
                    width: "100%",
                    padding: "8px",
                    marginBottom: "10px",
                    borderRadius: "4px",
                    border: "1px solid #ccc",
                }}
            />

            {/* Scrollable container */}
            <div
                style={{
                    maxHeight: "400px",
                    overflowY: "auto",
                    border: "1px solid #ccc",
                    borderRadius: "6px",
                    padding: "5px",
                }}
            >
                {/* All Countries at top */}
                {allOption && (
                    <div
                        key={allOption.value}
                        style={{
                            display: "flex",
                            alignItems: "center",
                            gap: "8px",
                            padding: "5px",
                            borderBottom: "1px solid #eee",
                            fontWeight: "bold",
                        }}
                    >
                        <input
                            type="checkbox"
                            checked={value.length === countries.length}
                            onChange={() => handleSelect("all")}
                            style={{ accentColor: "blue" }}
                        />
                        <label>{allOption.label}</label>
                    </div>
                )}

                {/* Individual countries */}
                {filteredCountries.map((c) => (
                    <div
                        key={c.value}
                        style={{
                            display: "flex",
                            alignItems: "center",
                            gap: "8px",
                            padding: "5px",
                            borderBottom: "1px solid #eee",
                        }}
                    >
                        <input
                            type="checkbox"
                            checked={value.includes(c.value)}
                            onChange={() => handleSelect(c.value)}
                            style={{ accentColor: "blue" }}
                        />
                        <label>{c.label}</label>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default CountrySelector;
