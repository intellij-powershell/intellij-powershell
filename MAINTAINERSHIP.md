<!--
SPDX-FileCopyrightText: 2023-2026 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>

SPDX-License-Identifier: Apache-2.0
-->

intellij-powershell Maintainership
==================================

Release
-------

To release a new version, follow these steps.

1. If required, update the copyright statements in the `README.md` and `plugin.xml` files, if required.
2. Choose the new version. It should consist of three numbers (i.e. `1.0.0`).
3. Change the version number in the `gradle.properties` (`pluginVersion=…`).
4. Make sure there's a properly formed version entry in the `CHANGELOG.md`.
5. Merge these changes via a PR.
6. Push a tag named `v<VERSION>` to GitHub.

The new release will be published automatically.

Update Token
------------

To update the token used for publishing the plugin, follow these steps.

1. Go to [the Marketplace token update section][marketplace.tokens].
2. Drop the old token called `github.intellij-powershell`.
3. Create a new one with the same name.
4. Go to [the GitHub repository's Secrets page][github.secrets].
5. Update the `JETBRAINS_MARKETPLACE_TOKEN` with the new value.

[github.secrets]: https://github.com/intellij-powershell/intellij-powershell/settings/secrets/actions
[marketplace.tokens]: https://plugins.jetbrains.com/author/me/tokens

Rotate Dependency Updater GitHub App Credentials
------------------------------------------------

This project uses a dedicated GitHub application to update IntelliJ-based dependencies, as documented in [intellij-updater](https://github.com/ForNeVeR/intellij-updater).

The workflow generates short-lived installation tokens at runtime from the configured GitHub App credentials.

To rotate the credentials:

1. Go to [the application settings](https://github.com/settings/apps/intellij-updater).
2. Generate a new private key.
3. Copy the **App ID** to the `IJ_UPDATER_APP_ID` variable in [GitHub Actions secrets][github.secrets].
4. Copy the new private key to the `IJ_UPDATER_PRIVATE_KEY` variable in [GitHub Actions secrets][github.secrets].
