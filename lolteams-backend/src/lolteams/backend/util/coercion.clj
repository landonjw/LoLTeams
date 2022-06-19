(ns lolteams.backend.util.coercion)

(defn string->int [str]
  (let [result (read-string str)]
    (if (number? result)
      result)))