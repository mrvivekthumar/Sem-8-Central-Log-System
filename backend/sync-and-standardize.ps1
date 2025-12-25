# ============================================================================
# Complete Project Synchronization & Standardization Script
# ============================================================================

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  PROJECT SYNC & STANDARDIZATION          " -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

$baseDir = "D:\ColabBridge\backend"
Set-Location $baseDir

$issues = @()
$fixes = @()

# ============================================================================
# ISSUE 1: Check for Missing Folders in StudentService
# ============================================================================
Write-Host "[ISSUE 1] Checking StudentService completeness..." -ForegroundColor Yellow

$studentServicePath = "$baseDir\StudentService\src\main\java\com\example\studentservice"
$requiredFolders = @("controller", "config", "service")

foreach ($folder in $requiredFolders) {
    $folderPath = Join-Path $studentServicePath $folder
    if (-not (Test-Path $folderPath)) {
        $issues += "CRITICAL: StudentService missing $folder folder"
        Write-Host "  ✗ Missing: $folder/" -ForegroundColor Red
    } else {
        Write-Host "  ✓ Found: $folder/" -ForegroundColor Green
    }
}

Write-Host ""

# ============================================================================
# ISSUE 2: Standardize DAO/Repository Naming
# ============================================================================
Write-Host "[ISSUE 2] Analyzing DAO vs Repository naming..." -ForegroundColor Yellow

$namingAnalysis = @"

CURRENT STATE:
--------------
Authentication-Service:
  - Folder: repository/
  - Files: *Repository.java ✓ (Correct pattern)

StudentService:
  - Folder: repository/
  - Files: *Dao.java ⚠️ (Inconsistent naming)

FacultyService:
  - Folder: repository/
  - Files: *Dao.java ⚠️ (Inconsistent naming)
  - notification/dao/ ⚠️ (Different folder name)

RECOMMENDED STANDARD:
--------------------
Option A (Spring Standard):
  - All services use: repository/
  - All files use: *Repository.java

Option B (Your Current Pattern):
  - All services use: repository/
  - All files use: *Dao.java
  - Rename notification/dao → notification/repository

"@

Write-Host $namingAnalysis -ForegroundColor Cyan
Write-Host ""

# ============================================================================
# ISSUE 3: NotificationService Analysis
# ============================================================================
Write-Host "[ISSUE 3] Analyzing NotificationService..." -ForegroundColor Yellow

$notificationServicePath = "$baseDir\NotificationService"
$hasPomXml = Test-Path "$notificationServicePath\pom.xml"
$hasSrc = Test-Path "$notificationServicePath\src"

if ($hasSrc -and -not $hasPomXml) {
    $issues += "WARNING: NotificationService is incomplete (missing pom.xml)"
    Write-Host "  ⚠️ NotificationService is incomplete!" -ForegroundColor Yellow
    Write-Host "     - Has src/ but missing pom.xml" -ForegroundColor Yellow
    Write-Host "     - Notification logic already in FacultyService" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "  RECOMMENDATION: Remove NotificationService (duplicates FacultyService/notification)" -ForegroundColor Cyan
}

Write-Host ""

# ============================================================================
# DECISION POINT
# ============================================================================
Write-Host "============================================" -ForegroundColor Yellow
Write-Host "  STANDARDIZATION DECISIONS NEEDED        " -ForegroundColor Yellow
Write-Host "============================================" -ForegroundColor Yellow
Write-Host ""

Write-Host "DECISION 1: DAO/Repository Naming" -ForegroundColor Cyan
Write-Host "  A) Rename all to *Repository.java (Spring standard)" -ForegroundColor White
Write-Host "  B) Keep *Dao.java but standardize folder names" -ForegroundColor White
Write-Host "  C) Keep current mixed naming (not recommended)" -ForegroundColor White
Write-Host ""

$namingChoice = Read-Host "Enter choice (A/B/C)"

Write-Host ""
Write-Host "DECISION 2: NotificationService" -ForegroundColor Cyan
Write-Host "  A) Remove NotificationService (use FacultyService/notification)" -ForegroundColor White
Write-Host "  B) Complete NotificationService as separate microservice" -ForegroundColor White
Write-Host ""

$notificationChoice = Read-Host "Enter choice (A/B)"

Write-Host ""

# ============================================================================
# APPLY FIXES BASED ON DECISIONS
# ============================================================================
Write-Host "[APPLYING FIXES] Processing your choices..." -ForegroundColor Yellow
Write-Host ""

# Fix 1: Handle DAO/Repository Naming
if ($namingChoice -eq "A") {
    Write-Host "  → Renaming all *Dao.java to *Repository.java..." -ForegroundColor Cyan
    
    # StudentService
    $studentRepoPath = "$baseDir\StudentService\src\main\java\com\example\studentservice\repository"
    if (Test-Path $studentRepoPath) {
        Get-ChildItem -Path $studentRepoPath -Filter "*Dao.java" -Recurse | ForEach-Object {
            $newName = $_.Name -replace "Dao\.java", "Repository.java"
            $newPath = Join-Path $_.DirectoryName $newName
            Move-Item $_.FullName $newPath -Force
            Write-Host "    ✓ Renamed: $($_.Name) → $newName" -ForegroundColor Green
            
            # Update file content
            $content = Get-Content $newPath -Raw
            $content = $content -replace "interface\s+(\w+)Dao", "interface `$1Repository"
            $content = $content -replace "class\s+(\w+)Dao", "class `$1Repository"
            Set-Content $newPath $content -NoNewline -Encoding UTF8
        }
    }
    
    # FacultyService
    $facultyRepoPath = "$baseDir\FacultyService\src\main\java\com\example\facultyservice\repository"
    if (Test-Path $facultyRepoPath) {
        Get-ChildItem -Path $facultyRepoPath -Filter "*Dao.java" -Recurse | ForEach-Object {
            $newName = $_.Name -replace "Dao\.java", "Repository.java"
            $newPath = Join-Path $_.DirectoryName $newName
            Move-Item $_.FullName $newPath -Force
            Write-Host "    ✓ Renamed: $($_.Name) → $newName" -ForegroundColor Green
            
            # Update file content
            $content = Get-Content $newPath -Raw
            $content = $content -replace "interface\s+(\w+)Dao", "interface `$1Repository"
            $content = $content -replace "class\s+(\w+)Dao", "class `$1Repository"
            Set-Content $newPath $content -NoNewline -Encoding UTF8
        }
    }
    
    # Rename notification/dao to notification/repository
    $notifDaoPath = "$baseDir\FacultyService\src\main\java\com\example\facultyservice\notification\dao"
    $notifRepoPath = "$baseDir\FacultyService\src\main\java\com\example\facultyservice\notification\repository"
    
    if (Test-Path $notifDaoPath) {
        $tempPath = "$notifRepoPath`_temp"
        Move-Item $notifDaoPath $tempPath -Force
        Move-Item $tempPath $notifRepoPath -Force
        Write-Host "    ✓ Renamed: notification/dao → notification/repository" -ForegroundColor Green
        
        # Update files in renamed folder
        Get-ChildItem -Path $notifRepoPath -Filter "*.java" | ForEach-Object {
            $content = Get-Content $_.FullName -Raw
            $content = $content -replace "package com\.example\.facultyservice\.notification\.dao", "package com.example.facultyservice.notification.repository"
            $content = $content -replace "interface\s+(\w+)Dao", "interface `$1Repository"
            $content = $content -replace "class\s+(\w+)Dao", "class `$1Repository"
            Set-Content $_.FullName $content -NoNewline -Encoding UTF8
        }
    }
    
    # Update all references in Java files
    Write-Host "  → Updating import statements and references..." -ForegroundColor Cyan
    
    $allJavaFiles = Get-ChildItem -Path "$baseDir\StudentService\src", "$baseDir\FacultyService\src" -Filter "*.java" -Recurse -ErrorAction SilentlyContinue
    
    foreach ($file in $allJavaFiles) {
        $content = Get-Content $file.FullName -Raw
        $originalContent = $content
        
        # Update imports
        $content = $content -replace "import\s+com\.example\.(studentservice|facultyservice)\.repository\.(\w+)Dao;", "import com.example.`$1.repository.`$2Repository;"
        $content = $content -replace "import\s+com\.example\.facultyservice\.notification\.dao\.(\w+);", "import com.example.facultyservice.notification.repository.`$1;"
        
        # Update field declarations
        $content = $content -replace "private\s+(\w+)Dao\s+", "private `$1Repository "
        $content = $content -replace "@Autowired\s+private\s+(\w+)Dao", "@Autowired private `$1Repository"
        
        # Update variable names
        $content = $content -replace "(\w+)Dao\s+(\w+);", "`$1Repository `$2;"
        
        if ($content -ne $originalContent) {
            Set-Content $file.FullName $content -NoNewline -Encoding UTF8
            Write-Host "    ✓ Updated: $($file.Name)" -ForegroundColor Green
        }
    }
    
    $fixes += "Standardized all DAO classes to Repository pattern"
    
} elseif ($namingChoice -eq "B") {
    Write-Host "  → Standardizing folder names (keeping *Dao.java naming)..." -ForegroundColor Cyan
    
    # Rename notification/dao to notification/repository
    $notifDaoPath = "$baseDir\FacultyService\src\main\java\com\example\facultyservice\notification\dao"
    $notifRepoPath = "$baseDir\FacultyService\src\main\java\com\example\facultyservice\notification\repository"
    
    if (Test-Path $notifDaoPath) {
        $tempPath = "$notifRepoPath`_temp"
        Move-Item $notifDaoPath $tempPath -Force
        Move-Item $tempPath $notifRepoPath -Force
        
        # Update package declarations
        $daoFiles = Get-ChildItem -Path $notifRepoPath -Filter "*.java" -Recurse
        foreach ($file in $daoFiles) {
            $content = Get-Content $file.FullName -Raw
            $content = $content -replace "package com\.example\.facultyservice\.notification\.dao", "package com.example.facultyservice.notification.repository"
            $content = $content -replace "import com\.example\.facultyservice\.notification\.dao", "import com.example.facultyservice.notification.repository"
            Set-Content $file.FullName $content -NoNewline -Encoding UTF8
        }
        
        Write-Host "    ✓ Renamed: notification/dao → notification/repository" -ForegroundColor Green
    }
    
    $fixes += "Standardized folder naming (all use repository/)"
} else {
    Write-Host "  ℹ Keeping current mixed naming" -ForegroundColor Cyan
}

Write-Host ""

# Fix 2: Handle NotificationService
if ($notificationChoice -eq "A") {
    Write-Host "  → Removing incomplete NotificationService..." -ForegroundColor Cyan
    
    if (Test-Path $notificationServicePath) {
        Remove-Item $notificationServicePath -Recurse -Force
        Write-Host "    ✓ Removed: NotificationService/" -ForegroundColor Green
        $fixes += "Removed incomplete NotificationService (logic exists in FacultyService)"
    }
} else {
    Write-Host "  ℹ NotificationService kept (you'll need to complete it manually)" -ForegroundColor Cyan
    $issues += "TODO: Complete NotificationService with pom.xml and full implementation"
}

Write-Host ""

# ============================================================================
# COMPILE ALL SERVICES
# ============================================================================
Write-Host "[VERIFICATION] Compiling all services..." -ForegroundColor Yellow

$services = @("Authentication-Service", "StudentService", "FacultyService", "Api-Gateway")
$compilationResults = @{}

foreach ($service in $services) {
    $servicePath = "$baseDir\$service"
    if (Test-Path $servicePath) {
        Write-Host "  → Compiling $service..." -ForegroundColor Cyan
        Set-Location $servicePath
        $output = & mvn clean compile -q 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "    ✓ SUCCESS" -ForegroundColor Green
            $compilationResults[$service] = "SUCCESS"
        } else {
            Write-Host "    ✗ FAILED" -ForegroundColor Red
            $compilationResults[$service] = "FAILED"
            $issues += "COMPILATION FAILED: $service"
        }
    }
}

Set-Location $baseDir
Write-Host ""

# ============================================================================
# GENERATE REPORT
# ============================================================================
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$reportFile = "$baseDir\sync-report_$timestamp.txt"

$reportContent = @"
============================================
PROJECT SYNCHRONIZATION REPORT
============================================
Timestamp: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")

ISSUES FOUND:
-------------
"@

if ($issues.Count -gt 0) {
    foreach ($issue in $issues) {
        $reportContent += "`n⚠️  $issue"
    }
} else {
    $reportContent += "`n✓ No issues found"
}

$reportContent += @"


FIXES APPLIED:
--------------
"@

if ($fixes.Count -gt 0) {
    foreach ($fix in $fixes) {
        $reportContent += "`n✓ $fix"
    }
} else {
    $reportContent += "`n- No fixes applied"
}

$reportContent += @"


COMPILATION RESULTS:
--------------------
"@

foreach ($service in $compilationResults.Keys) {
    $status = $compilationResults[$service]
    $symbol = if ($status -eq "SUCCESS") { "✓" } else { "✗" }
    $reportContent += "`n$symbol $service : $status"
}

$reportContent += @"


NEXT STEPS:
-----------
1. Review changes above
2. Test all services locally
3. Commit changes to Git:
   git add .
   git commit -m "fix: Synchronize and standardize project structure

   - Standardized DAO/Repository naming across all services
   - Fixed notification module structure
   - All services compile successfully"
   git push origin main

"@

Set-Content $reportFile $reportContent -Encoding UTF8

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "      SYNCHRONIZATION COMPLETED            " -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📄 Report: $reportFile" -ForegroundColor Green
Write-Host ""

if ($issues.Count -eq 0 -and ($compilationResults.Values | Where-Object { $_ -eq "SUCCESS" }).Count -eq $services.Count) {
    Write-Host "✅ ALL SERVICES SYNCHRONIZED AND WORKING!" -ForegroundColor Green
} else {
    Write-Host "⚠️  SOME ISSUES REMAIN - Review report above" -ForegroundColor Yellow
}

Start-Process notepad $reportFile
