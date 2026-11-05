#!/usr/bin/env python3
"""Validate public Firebase config against the app ID. Requires only Python 3."""
import json
from pathlib import Path

root = Path(__file__).resolve().parents[1]
config = json.loads((root / "app/google-services.json").read_text(encoding="utf-8"))
project = config["project_info"]
android_package = "ir.hatef.app"
assert project["project_id"] == "hatef-platform", "Unexpected Firebase project"
clients = config["client"]
matches = [client for client in clients
           if client["client_info"]["android_client_info"]["package_name"] == android_package]
assert len(matches) == 1, "Exactly one matching Android client is required"
build_script = (root / "app/build.gradle.kts").read_text(encoding="utf-8")
assert 'applicationId = "ir.hatef.app"' in build_script, "Android application ID does not match"
assert "private_key" not in json.dumps(config), "Never commit service-account private keys"
print(f"Firebase configuration is valid for {android_package} (project {project['project_id']}).")
