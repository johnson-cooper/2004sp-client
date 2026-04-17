package main

import (
	"database/sql"
	"encoding/json"
	"log"
	"os"
	"path/filepath"

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

// resolveDbPath looks for db.sqlite in the following order:
//  1. Path set in config.json (next to the exe)
//  2. Common relative paths from the exe location
//
// If found via auto-detection, the path is saved to config.json so
// future runs skip the search. Returns "" if nothing is found.
func resolveDbPath() string {
	exeDir := exeDir()
	configPath := filepath.Join(exeDir, "config.json")

	// 1. Check config.json for a saved path.
	if data, err := os.ReadFile(configPath); err == nil {
		var cfg struct {
			DbPath string `json:"db_path"`
		}
		if json.Unmarshal(data, &cfg) == nil && cfg.DbPath != "" {
			if _, err := os.Stat(cfg.DbPath); err == nil {
				log.Printf("[db] using path from config.json: %s", cfg.DbPath)
				return cfg.DbPath
			}
			log.Printf("[db] config.json path not found: %s", cfg.DbPath)
		}
	}

	// 2. Search common locations relative to the exe.
	candidates := []string{
		filepath.Join(exeDir, "db.sqlite"),
		filepath.Join(exeDir, "..", "db.sqlite"),
		filepath.Join(exeDir, "..", "engine", "db.sqlite"),
		filepath.Join(exeDir, "..", "..", "engine", "db.sqlite"),
	}
	for _, p := range candidates {
		abs, _ := filepath.Abs(p)
		if _, err := os.Stat(abs); err == nil {
			log.Printf("[db] auto-detected database: %s", abs)
			saveDbPathConfig(configPath, abs)
			return abs
		}
	}

	log.Printf("[db] db.sqlite not found — create config.json next to the exe with {\"db_path\": \"<path to db.sqlite>\"}")
	return ""
}

func saveDbPathConfig(configPath, dbPath string) {
	data, _ := json.MarshalIndent(map[string]string{"db_path": dbPath}, "", "  ")
	if err := os.WriteFile(configPath, data, 0644); err != nil {
		log.Printf("[db] could not save config.json: %v", err)
	}
}

func exeDir() string {
	exe, err := os.Executable()
	if err != nil {
		return "."
	}
	return filepath.Dir(exe)
}

func (h *HiscoreService) Init() {
	path := resolveDbPath()
	if path == "" {
		return
	}
	db, err := sql.Open("sqlite", path)
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