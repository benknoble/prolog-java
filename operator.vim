nnoremap <buffer> \o :Operator<Space>
command -nargs=+ Operator call <SID>operator(<f-args>)

function s:operator(...)
  const [clazz, base, name; _] = a:000
  const pattern = substitute(name, '[<>]', '`&', 'g')
  const lines = [
        \ printf('class %s extends %s {', clazz, base),
        \ printf('%s(ParseTreePatternMatcher m) {', clazz),
        \ pattern is# name
        \   ? printf('super("%s", m);', name)
        \   : printf('super("%s", "%s", m);', name, pattern),
        \ '}',
        \ '}',
        \ ]
  put =lines
  normal! =%
endfunction
