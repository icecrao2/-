// 해당 틀에서 수정해서 테스트하면됨

import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
      ),
      home: MyHomePage(),
    );
  }
}








class MyHomePage extends StatelessWidget {
  final data = ValueNotifier(true);

  MyHomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text('test'),
      ),
      body: Center(
        child: Column(
          children: [
            ListenableBuilder(
                listenable: data,
                builder: (_, __)  {
                  print('parent rebuild');
                  if(data.value) {
                    return ParentWidget(data: data);
                  }
                  return SizedBox.shrink();
                }
            ),

            ElevatedButton(
              onPressed: () => data.value = !(data.value),
              child: Text('changed!')
            )
          ],
        )
      ),
    );
  }
}



class ParentWidget extends StatefulWidget {
  final ValueNotifier data;

  const ParentWidget({super.key, required this.data});

  @override
  State<ParentWidget> createState() => _ParentWidgetState();
}

class _ParentWidgetState extends State<ParentWidget> {

  @override
  void dispose() {
    print('parent dispose');
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      child: ChildWidget(data: widget.data,),
    );
  }
}





class ChildWidget extends StatefulWidget {
  final ValueNotifier data;

  const ChildWidget({super.key, required this.data});

  @override
  State<ChildWidget> createState() => _ChildWidgetState();
}

class _ChildWidgetState extends State<ChildWidget> {

  @override
  void dispose() {
    print('child dispose');
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ListenableBuilder(
      listenable: widget.data,
      builder: (_, __) {
        print('child rebuild');
        return Container(
          width: 300,
          height: 300,
          color: widget.data.value ? Colors.red : Colors.blue,
        );
      }
    );
  }
}
