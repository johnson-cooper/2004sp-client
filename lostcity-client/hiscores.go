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
	XP       int    `json:"xp"`
	Type     int    `json:"type"`
}

type HiscoreService struct {
	db *sql.DB
}

func NewHiscoreService() *HiscoreService {
	return &HiscoreService{}
}

func (h *HiscoreService) Init() {
	if cfg.DbPath == "" {
		return
	}
	db, err := sql.Open("sqlite", cfg.DbPath)
	if err != nil {
		log.Printf("[db] failed to open database: %v", err)
		return
	}
	h.db = db
}

// GetHiscores returns the overall hiscores (total level) from hiscore_large.
func (h *HiscoreService) GetHiscores() string {
	if h.db == nil {
		return "[]"
	}
	rows, err := h.db.Query(`
		SELECT a.username, hl.level, hl.value
		FROM hiscore_large hl
		JOIN account a ON a.id = hl.account_id
		WHERE hl.profile = 'main' AND hl.type = 0
		ORDER BY hl.level DESC, hl.value DESC
		LIMIT 25
	`)
	if err != nil {
		log.Printf("[hiscores] GetHiscores: %v", err)
		return "[]"
	}
	defer rows.Close()
	return marshalEntries(rows, 0)
}

// GetHiscoresByType returns hiscores for a specific skill type.
func (h *HiscoreService) GetHiscoresByType(skillType int) string {
	if h.db == nil {
		return "[]"
	}
	rows, err := h.db.Query(`
		SELECT a.username, h.level, h.value
		FROM hiscore h
		JOIN account a ON a.id = h.account_id
		WHERE h.profile = 'main' AND h.type = ?
		ORDER BY h.level DESC, h.value DESC
		LIMIT 25
	`, skillType)
	if err != nil {
		log.Printf("[hiscores] GetHiscoresByType(%d): %v", skillType, err)
		return "[]"
	}
	defer rows.Close()
	return marshalEntries(rows, skillType)
}

func marshalEntries(rows *sql.Rows, skillType int) string {
	var entries []HiscoreEntry
	rank := 1
	for rows.Next() {
		var e HiscoreEntry
		if err := rows.Scan(&e.Username, &e.Level, &e.XP); err != nil {
			continue
		}
		e.Rank = rank
		e.Type = skillType
		entries = append(entries, e)
		rank++
	}
	data, _ := json.Marshal(entries)
	return string(data)
}