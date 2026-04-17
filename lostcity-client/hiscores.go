package main

import (
	"database/sql"
	"encoding/json"
	"log"

	_ "github.com/glebarez/sqlite"
)

type HiscoreEntry struct {
	Rank     int    `json:"rank"`
	Username string `json:"username"`
	Level    int    `json:"level"`
	Type    int    `json:"type"`
}

type HiscoreService struct {
	db *sql.DB
}

func NewHiscoreService() *HiscoreService {
	return &HiscoreService{}
}

func (h *HiscoreService) Init(dbPath string) error {
	var err error
	h.db, err = sql.Open("sqlite", dbPath)
	if err != nil {
		log.Printf("Failed to open database: %v", err)
		return err
	}
	return nil
}

func (h *HiscoreService) GetHiscores() string {
	if h.db == nil {
		return "[]"
	}

	rows, err := h.db.Query(`
		SELECT h.account_id, a.username, h.type, h.level
		FROM hiscore h
		LEFT JOIN account a ON h.account_id = a.id
		WHERE h.profile = 'main' AND h.type = 0
		ORDER BY h.level DESC
		LIMIT 20
	`)
	if err != nil {
		log.Printf("Query failed: %v", err)
		return "[]"
	}
	defer rows.Close()

	var entries []HiscoreEntry
	rank := 1
	for rows.Next() {
		var entry HiscoreEntry
		if err := rows.Scan(&entry.Rank, &entry.Username, &entry.Type, &entry.Level); err != nil {
			continue
		}
		entry.Rank = rank
		entries = append(entries, entry)
		rank++
	}

	data, _ := json.Marshal(entries)
	return string(data)
}

func (h *HiscoreService) GetHiscoresByType(skillType int) string {
	if h.db == nil {
		return "[]"
	}

	rows, err := h.db.Query(`
		SELECT h.account_id, a.username, h.type, h.level
		FROM hiscore h
		LEFT JOIN account a ON h.account_id = a.id
		WHERE h.profile = 'main' AND h.type = ?
		ORDER BY h.level DESC
		LIMIT 20
	`, skillType)
	if err != nil {
		return "[]"
	}
	defer rows.Close()

	var entries []HiscoreEntry
	rank := 1
	for rows.Next() {
		var entry HiscoreEntry
		if err := rows.Scan(&entry.Rank, &entry.Username, &entry.Type, &entry.Level); err != nil {
			continue
		}
		entry.Rank = rank
		entries = append(entries, entry)
		rank++
	}

	data, _ := json.Marshal(entries)
	return string(data)
}