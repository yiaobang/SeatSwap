# 🎲 SeatSwap - Smart Multilingual Seating Generator
[🇨🇳 简体中文](./README.MD) | [🇯🇵 日本語](./README_JA.MD) | [🇺🇸 English](./README_EN.MD)

Welcome to **SeatSwap**! This is the ultimate automatic seating chart generator designed for teachers, event organizers, and managers.
Whether you want a completely random shuffle for your classroom, or you need to manage international students with long bilingual names, this tool has covered you!

---

## ✨ Key Features

* **Crypto-Grade Fairness**: Powered by a secure cryptographic random algorithm, ensuring 100% unbiased results for every seat swap.
* **Smart Special Rules**: Lock specific students into a designated row or column while the rest of the seats are filled randomly.
* **🌍 Perfect for International Classes (Auto Word-Wrap)**：For students with both local and English names (e.g., `Wang(Leo)`), simply use parentheses `()`. The system will **automatically split the name into two lines** inside the seat box, keeping the chart beautifully aligned and easy to read!
* **Seamless Multilingual Support**: Switch between **English, Chinese, and Japanese** instantly with a clean, responsive UI.

---

## 🛠️ Preparation (For Absolute Beginners)

Before running the swap, prepare two simple `.txt` files using Notepad or TextEdit.

### 1. Total Roster File (e.g., `students.txt`)
Write one student's name per line. Avoid extra spaces.
> **💡 Pro Tip for International Names:**
> Use parentheses `()` to add English names or aliases. The app will neatly stack them vertically!
>
> **Example:**
> John
> Tanaka(Sam)
> Michael(Mick)
> David

### 2. Special Needs File (e.g., `rules.txt` *Optional*)
* To assign someone to a specific **Row**: Use `R` + `Row Number` + `.` + `Name`
* To assign someone to a specific **Column**: Use `C` + `Column Number` + `.` + `Name`
> **⚠️ Note: If a name contains parentheses, the name in the rules file MUST match the roster file exactly.**
>
> **Example:**
> R1.John  *(John must be placed somewhere in Row 1)*
> C3.Tanaka(Sam)    *(Tanaka with his alias must be placed somewhere in Column 3)*

---

## 🚀 How to Use (Step-by-Step Guide)

1.  **Launch the App**: Double-click to open the application.
2.  **Select Language**: Choose `English` from the `🌐 Language` menu on the top left.
3.  **Set Grid Dimensions**: Enter `Rows` and `Cols` based on your classroom size (e.g., 5 Rows and 6 Cols).
4.  **Import Your Files**:
    * Click **[Import Total Roster (.txt)]** and select your `students.txt`.
    * Click **[Import Special Needs (.txt)]** and select your `rules.txt`.
5.  **Run Seating Swap**: Click the large **[🎲 Run Seat Swap]** button.
6.  **Enjoy the Result**: The grid on the right updates instantly. Special rules are marked with a `★` icon, and bilingual names with `()` are elegantly displayed in a two-line layout.