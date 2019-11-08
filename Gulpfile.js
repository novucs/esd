var gulp = require('gulp');
var sass = require('gulp-sass');

// Gulp Styles compiling
gulp.task('default', function (done) {
  gulp.src('src/main/webapp/scss/*.scss')
  .pipe(sass({outputStyle: 'compressed'}).on('error', sass.logError))
  .pipe(gulp.dest('src/main/webapp/css/'));
  done();
});
